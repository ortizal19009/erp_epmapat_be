package com.epmapat.erp_epmapat.servicio;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import javax.transaction.Transactional;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.DTO.FecMailQueueRequestDto;
import com.epmapat.erp_epmapat.emails.dtos.AttachmentInput;
import com.epmapat.erp_epmapat.emails.dtos.SendEmailRequest;
import com.epmapat.erp_epmapat.emails.model.EmailMessage;
import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.repository.EmailMessageR;
import com.epmapat.erp_epmapat.emails.service.EmailComposerService;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.modelo.Fec_mail_queue;
import com.epmapat.erp_epmapat.repositorio.Fec_mail_queueR;
import com.epmapat.erp_epmapat.sri.services.XmlToPdfService;

@Service
public class FecMailQueueService {
   private static final int MAX_INTENTOS = 5;
   private static final int LOTE = 20;
   private static final Set<String> ESTADOS_ACTIVOS = Set.of("PENDIENTE", "ENVIANDO", "REINTENTO");

   private final Fec_mail_queueR queueR;
   private final Fec_facturaService fecFacturaService;
   private final EmailComposerService emailComposerService;
   private final EmailMessageR emailMessageR;
   private final XmlToPdfService xmlToPdfService;
   private final FecFacturaLogService logService;

   public FecMailQueueService(
         Fec_mail_queueR queueR,
         Fec_facturaService fecFacturaService,
         EmailComposerService emailComposerService,
         EmailMessageR emailMessageR,
         XmlToPdfService xmlToPdfService,
         FecFacturaLogService logService) {
      this.queueR = queueR;
      this.fecFacturaService = fecFacturaService;
      this.emailComposerService = emailComposerService;
      this.emailMessageR = emailMessageR;
      this.xmlToPdfService = xmlToPdfService;
      this.logService = logService;
   }

   @Transactional
   public Map<String, Object> enqueue(FecMailQueueRequestDto request) {
      List<Long> ids = request.getIdfacturas() == null ? List.of() : request.getIdfacturas();
      Map<String, Object> response = new HashMap<>();
      List<Long> encoladas = new ArrayList<>();
      List<Long> omitidas = new ArrayList<>();

      for (Long idfactura : ids) {
         Optional<Fec_factura> optionalFactura = fecFacturaService.findById(idfactura);
         if (optionalFactura.isEmpty()) {
            omitidas.add(idfactura);
            continue;
         }

         Fec_factura factura = optionalFactura.get();
         if (!estaListaParaCola(factura)) {
            omitidas.add(idfactura);
            continue;
         }
         if (!queueR.findActivasByFactura(idfactura, ESTADOS_ACTIVOS).isEmpty()) {
            omitidas.add(idfactura);
            continue;
         }

         Fec_mail_queue item = new Fec_mail_queue();
         item.setIdfactura(idfactura);
         item.setCorreo(factura.getEmailcomprador().trim());
         item.setEstado("PENDIENTE");
         item.setIntentos(0);
         item.setUsuarioSolicita(request.getUsuarioSolicita());
         item.setPrioridad(request.getPrioridad() == null ? 1 : request.getPrioridad());
         item.setIpSolicita(request.getIpSolicita());
         queueR.save(item);

         factura.setEmailEstado("PENDIENTE");
         factura.setMailError(null);
         factura.setMailEnviado(Boolean.FALSE);
         fecFacturaService.save(factura);
         logService.registrar(idfactura, "MQ", "Factura encolada para reenvio de correo");
         encoladas.add(idfactura);
      }

      response.put("encoladas", encoladas);
      response.put("omitidas", omitidas);
      response.put("total", ids.size());
      return response;
   }

   @Transactional
   public void procesarPendientes() {
      List<Fec_mail_queue> items = queueR.lockNextByEstados(List.of("PENDIENTE", "REINTENTO"), PageRequest.of(0, LOTE));
      for (Fec_mail_queue item : items) {
         procesarItem(item);
      }
   }

   @Transactional
   public void sincronizarEstadosOutbox() {
      List<Fec_mail_queue> items = queueR.lockNextByEstados(List.of("ENVIANDO"), PageRequest.of(0, LOTE * 5));
      for (Fec_mail_queue item : items) {
         if (item.getCorrelationId() == null || item.getCorrelationId().isBlank()) {
            continue;
         }
         Optional<EmailMessage> opt = emailMessageR.findFirstByCorrelationIdOrderByCreatedAtDesc(item.getCorrelationId());
         if (opt.isEmpty()) {
            continue;
         }

         EmailMessage email = opt.get();
         Fec_factura factura = fecFacturaService.findById(item.getIdfactura()).orElse(null);
         if (factura == null) {
            continue;
         }

         if (email.getStatus() == EmailStatus.SENT) {
            item.setEstado("ENVIADO");
            item.setFechaEnvio(LocalDateTime.now());
            item.setUltimoError(null);
            queueR.save(item);
            factura.setMailIntentos(item.getIntentos());
            fecFacturaService.marcarCorreoEnviado(factura);
            logService.registrar(factura.getIdfactura(), "MAIL", "Correo enviado por cola asincrona");
         } else if (email.getStatus() == EmailStatus.FAILED) {
            String lastError = email.getLastError();
            item.setUltimoError(lastError);
            item.setEstado(item.getIntentos() >= MAX_INTENTOS ? "ERROR" : "REINTENTO");
            queueR.save(item);

            factura.setMailEnviado(Boolean.FALSE);
            factura.setMailIntentos(item.getIntentos());
            factura.setMailError(lastError);
            factura.setEmailEstado(item.getEstado().equals("ERROR") ? "ERROR_ENVIO" : "REINTENTO");
            fecFacturaService.save(factura);
            logService.registrar(factura.getIdfactura(), "MAIL", "Fallo correo en cola: " + lastError);
         }
      }
   }

   private void procesarItem(Fec_mail_queue item) {
      Fec_factura factura = fecFacturaService.findById(item.getIdfactura()).orElse(null);
      if (factura == null || !estaListaParaCola(factura)) {
         item.setEstado("CANCELADO");
         item.setUltimoError("Factura no disponible o sin XML/correo valido");
         queueR.save(item);
         return;
      }

      try {
         item.setEstado("ENVIANDO");
         item.setIntentos((item.getIntentos() == null ? 0 : item.getIntentos()) + 1);
         item.setUltimoError(null);

         String correlationId = "FACTURA-QUEUE-" + factura.getIdfactura() + "-" + item.getId() + "-" + System.currentTimeMillis();
         item.setCorrelationId(correlationId);
         queueR.save(item);

         SendEmailRequest request = new SendEmailRequest();
         request.to = List.of(item.getCorreo());
         request.subject = "Factura electronica " + factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
         request.html = buildFacturaEmailHtml(factura);
         request.text = stripHtml(request.html);
         request.correlationId = correlationId;
         request.attachments = buildAttachments(factura);

         emailComposerService.enqueue(EmailType.DOC_ELECTRONICO, request);

         factura.setMailIntentos(item.getIntentos());
         factura.setMailError(null);
         factura.setEmailEstado("PENDIENTE");
         fecFacturaService.save(factura);
      } catch (Exception e) {
         item.setUltimoError(e.getMessage());
         item.setEstado(item.getIntentos() >= MAX_INTENTOS ? "ERROR" : "REINTENTO");
         queueR.save(item);

         factura.setMailEnviado(Boolean.FALSE);
         factura.setMailIntentos(item.getIntentos());
         factura.setMailError(e.getMessage());
         factura.setEmailEstado(item.getEstado().equals("ERROR") ? "ERROR_ENVIO" : "REINTENTO");
         fecFacturaService.save(factura);
      }
   }

   private boolean estaListaParaCola(Fec_factura factura) {
      return factura != null
            && factura.getIdfactura() != null
            && factura.getEmailcomprador() != null
            && !factura.getEmailcomprador().isBlank()
            && factura.getXmlautorizado() != null
            && !factura.getXmlautorizado().isBlank()
            && Set.of("A", "O", "X", "C").contains(String.valueOf(factura.getEstado()));
   }

   private List<AttachmentInput> buildAttachments(Fec_factura factura) {
      List<AttachmentInput> attachments = new ArrayList<>();
      ByteArrayOutputStream pdf = xmlToPdfService.generarFacturaPDF(factura.getXmlautorizado());
      attachments.add(toAttachment("factura-" + factura.getSecuencial() + ".pdf", "application/pdf", pdf.toByteArray()));
      attachments.add(toAttachment("factura-" + factura.getSecuencial() + ".xml", "application/xml",
            factura.getXmlautorizado().getBytes(java.nio.charset.StandardCharsets.UTF_8)));
      return attachments;
   }

   private AttachmentInput toAttachment(String name, String contentType, byte[] content) {
      AttachmentInput input = new AttachmentInput();
      input.name = name;
      input.contentType = contentType;
      input.base64 = Base64.getEncoder().encodeToString(content);
      return input;
   }

   private String stripHtml(String value) {
      return String.valueOf(value == null ? "" : value).replaceAll("<[^>]*>", " ").replaceAll("\\s+", " ").trim();
   }

   private String buildFacturaEmailHtml(Fec_factura factura) {
      String numero = factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
      return "<div style='font-family:Arial,sans-serif;color:#333'>"
            + "<h3>Factura electronica autorizada</h3>"
            + "<p>Estimado/a <strong>" + safe(factura.getRazonsocialcomprador()) + "</strong>,</p>"
            + "<p>Adjuntamos su comprobante electronico <strong>" + numero + "</strong>.</p>"
            + "<p>Clave de acceso: " + safe(factura.getClaveacceso()) + "</p>"
            + "<p>Este envio fue gestionado desde la cola asincrona del ERP.</p>"
            + "</div>";
   }

   private String safe(String value) {
      return value == null ? "" : value;
   }
}
