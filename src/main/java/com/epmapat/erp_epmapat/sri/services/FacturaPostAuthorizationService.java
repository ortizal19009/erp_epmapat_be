package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.emails.model.EmailAccount;
import com.epmapat.erp_epmapat.emails.model.EmailType;
import com.epmapat.erp_epmapat.emails.service.EmailAccountService;
import com.epmapat.erp_epmapat.interfaces.DefinirProjection;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.servicio.FecFacturaLogService;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;

@Service
public class FacturaPostAuthorizationService {
   @org.springframework.beans.factory.annotation.Autowired
   private org.springframework.jdbc.core.JdbcTemplate jdbc;
   private static final LocalDate FECHA_LIMITE_PDF = LocalDate.of(2025, 5, 6);

   private final Fec_facturaService fecFacturaService;
   private final XmlToPdfService xmlToPdfService;
   private final EmailService emailService;
   private final EmailAccountService emailAccountService;
   private final DefinirServicio definirServicio;
   private final FecFacturaLogService logService;
   private final CorreosEnviadosServicio correosEnviadosServicio;

   public FacturaPostAuthorizationService(
         Fec_facturaService fecFacturaService,
         XmlToPdfService xmlToPdfService,
         EmailService emailService,
         EmailAccountService emailAccountService,
         DefinirServicio definirServicio,
         FecFacturaLogService logService,
         CorreosEnviadosServicio correosEnviadosServicio) {
      this.fecFacturaService = fecFacturaService;
      this.xmlToPdfService = xmlToPdfService;
      this.emailService = emailService;
      this.emailAccountService = emailAccountService;
      this.definirServicio = definirServicio;
      this.logService = logService;
      this.correosEnviadosServicio = correosEnviadosServicio;
   }

   @org.springframework.transaction.annotation.Transactional
   public void procesarFacturaAutorizada(Fec_factura factura) {
      if(factura==null || factura.getIdfactura()==null || !"X".equals(factura.getEstado())
            || Boolean.TRUE.equals(factura.getMailEnviado()))return;
      if(factura.getXmlautorizado()==null || factura.getXmlautorizado().isBlank())
         throw new IllegalArgumentException("Falta XML autorizado para notificar");
      int changed=jdbc.update("UPDATE public.fec_factura SET estado='O',mail_enviado=false WHERE idfactura=? AND estado='X' AND mail_enviado IS DISTINCT FROM TRUE",factura.getIdfactura());
      if(changed==0)return;
      jdbc.update("INSERT INTO public.evento_outbox(factura_id,version) SELECT idfactura,notificacion_version FROM public.fec_factura WHERE idfactura=? ON CONFLICT(factura_id,tipo,version) DO NOTHING",factura.getIdfactura());
      factura.setEstado("O");factura.setMailEnviado(false);
      logService.registrar(factura.getIdfactura(),"O","Autorizada; notificacion delegada a la outbox de msvc-sri");
   }

   @org.springframework.transaction.annotation.Transactional
   public void procesarFacturaAutorizada(Long idfactura) {
      Optional<Fec_factura> factura = fecFacturaService.findById(idfactura);
      factura.ifPresent(this::procesarFacturaAutorizada);
   }

   private void validarFacturaLista(Fec_factura factura) {
      if (factura.getXmlautorizado() == null || factura.getXmlautorizado().isBlank()) {
         throw new IllegalArgumentException("La factura no tiene XML autorizado");
      }
      if (factura.getEmailcomprador() == null || factura.getEmailcomprador().isBlank()) {
         throw new IllegalArgumentException("La factura no tiene email del comprador");
      }
   }

   private byte[] generarPdf(Fec_factura factura) {
      ByteArrayOutputStream pdfStream;
      LocalDate fechaEmision = factura.getFechaemision() == null ? LocalDate.now() : factura.getFechaemision().toLocalDate();
      if (fechaEmision.isBefore(FECHA_LIMITE_PDF)) {
         pdfStream = xmlToPdfService.generarFacturaPDF_v2(factura.getXmlautorizado());
      } else {
         pdfStream = xmlToPdfService.generarFacturaPDF(factura.getXmlautorizado());
      }
      byte[] pdf = pdfStream.toByteArray();
      if (pdf.length == 0) {
         throw new IllegalStateException("El PDF generado esta vacio");
      }
      logService.registrar(factura.getIdfactura(), "X", "PDF generado correctamente");
      return pdf;
   }

   private String construirAsunto(DefinirProjection definir, Fec_factura factura) {
      String asunto = definir.getAsunto();
      if (asunto == null || asunto.isBlank()) {
         asunto = "Factura electronica {numero}";
      }
      return reemplazarVariables(asunto, factura);
   }

   private String construirMensaje(DefinirProjection definir, Fec_factura factura) {
      String texto = definir.getTextomail();
      if (texto == null || texto.isBlank()) {
         texto = "<p>Estimado cliente, adjuntamos su factura electronica {numero}.</p>";
      }
      return reemplazarVariables(texto, factura);
   }

   private String reemplazarVariables(String texto, Fec_factura factura) {
      String numero = factura.getEstablecimiento() + "-" + factura.getPuntoemision() + "-" + factura.getSecuencial();
      return texto
            .replace("{numero}", numero)
            .replace("{cliente}", Optional.ofNullable(factura.getRazonsocialcomprador()).orElse(""))
            .replace("{claveAcceso}", Optional.ofNullable(factura.getClaveacceso()).orElse(""));
   }
}
