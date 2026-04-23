package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.Fec_retencionesServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;

@Service
public class RetencionProcesamientoSRIService {

   private final RetencionSRIService retencionSRIService;
   private final RetencionPdfService retencionPdfService;
   private final RetencionEmailService retencionEmailService;
   private final RetencionesServicio retencionesServicio;
   private final Fec_retencionesServicio fecRetencionesServicio;
   private final RetencionesR retencionesR;
   private final RestTemplate restTemplate;
   private final CorreosEnviadosServicio correosEnviadosServicio;

   @Value("${sri.microservice.base-url:http://192.168.0.33:9090}")
   private String sriMicroserviceBaseUrl;

   public RetencionProcesamientoSRIService(
         RetencionSRIService retencionSRIService,
         RetencionPdfService retencionPdfService,
         RetencionEmailService retencionEmailService,
         RetencionesServicio retencionesServicio,
         Fec_retencionesServicio fecRetencionesServicio,
         RetencionesR retencionesR,
         RestTemplate restTemplate,
         CorreosEnviadosServicio correosEnviadosServicio) {
      this.retencionSRIService = retencionSRIService;
      this.retencionPdfService = retencionPdfService;
      this.retencionEmailService = retencionEmailService;
      this.retencionesServicio = retencionesServicio;
      this.fecRetencionesServicio = fecRetencionesServicio;
      this.retencionesR = retencionesR;
      this.restTemplate = restTemplate;
      this.correosEnviadosServicio = correosEnviadosServicio;
   }

   @Transactional
   public Map<String, Object> procesar(Long idretencion, String destinatario, String asunto, String mensaje) {
      Retenciones retencion = retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retencion " + idretencion));

      Map<String, Object> duplicada = validarRetencionYaProcesada(retencion);
      if (duplicada != null) {
         return duplicada;
      }

      var generada = retencionSRIService.generarYGuardar(idretencion);
      String xmlPlano = generada.getXmlautorizado();
      if (!StringUtils.hasText(xmlPlano)) {
         throw new IllegalStateException("No se pudo generar el XML de la retencion " + idretencion);
      }

      AutorizacionResultado resultado = enviarFirmarYAutorizar(xmlPlano);
      if (resultado.limiteDiario) {
         retencionSRIService.actualizarEstado(idretencion, "LIMITE_INTENTOS_DIARIO", resultado.mensaje);
         return Map.of(
               "idretencion", idretencion,
               "estado", "LIMITE_INTENTOS_DIARIO",
               "detalle", resultado.mensaje,
               "xmlautorizado", "",
               "errores", resultado.detalle);
      }
      if (resultado.pendiente) {
         retencionSRIService.actualizarEstado(idretencion, "PENDIENTE_AUTORIZACION", resultado.mensaje);
         return Map.of(
               "idretencion", idretencion,
               "estado", "PENDIENTE_AUTORIZACION",
               "detalle", resultado.mensaje,
               "xmlautorizado", "");
      }

      String xmlAutorizado = resultado.xmlAutorizado;
      if (!StringUtils.hasText(xmlAutorizado)) {
         throw new IllegalStateException("El SRI no devolvio una autorizacion para la retencion " + idretencion);
      }

      String numeroAutorizacion = extraerTag(xmlAutorizado, "numeroAutorizacion");
      String fechaAutorizacion = extraerTag(xmlAutorizado, "fechaAutorizacion");

      aplicarAutorizacion(retencion, xmlAutorizado, numeroAutorizacion, fechaAutorizacion);
      retencion.setEstado(1);
      retencionesServicio.updateRetencion(idretencion, retencion);

      retencionSRIService.actualizarXmlAutorizado(idretencion, xmlAutorizado, "AUTORIZADA", null);

      Map<String, Object> mail = retencionEmailService.enviarRetencion(
            idretencion,
            destinatario,
            asunto,
            mensaje,
            false);

      correosEnviadosServicio.registrarEnvio(
            "RETENCIONES",
            idretencion,
            "RETENCION",
            safeValue(mail.get("email"), destinatario),
            safeValue(asunto, "Comprobante de retencion"),
            "sri",
            safeValue(mail.get("archivo"), "retencion.pdf"),
            "ENVIADO",
            "Retencion autorizada y enviada por correo");

      return Map.of(
            "idretencion", idretencion,
            "estado", "AUTORIZADA",
            "numeroAutorizacion", numeroAutorizacion,
            "fechaAutorizacion", fechaAutorizacion,
            "xmlautorizado", xmlAutorizado,
            "email", safeValue(mail.get("email"), destinatario));
   }

   private Map<String, Object> validarRetencionYaProcesada(Retenciones retencion) {
      String claveAcceso = firstNonBlank(retencion.getClaveacceso(), "");
      String numeroAutorizacion = firstNonBlank(retencion.getNumautoriza_e(), retencion.getNumautoriza(), "");
      Integer estado = retencion.getEstado();

      if (estado != null && estado == 1 && StringUtils.hasText(numeroAutorizacion)) {
         return Map.of(
               "idretencion", retencion.getIdrete(),
               "estado", "YA_AUTORIZADA",
               "detalle", "La retención ya fue enviada/autorizada previamente.",
               "claveAcceso", claveAcceso,
               "numeroAutorizacion", numeroAutorizacion);
      }

      if (StringUtils.hasText(claveAcceso)) {
         boolean existeDuplicada = fecRetencionesServicio.existsByClaveaccesoAndEstadoIn(
               claveAcceso,
               List.of("AUTORIZADA", "ENVIADA", "PENDIENTE_AUTORIZACION"));
         if (existeDuplicada) {
            return Map.of(
                  "idretencion", retencion.getIdrete(),
                  "estado", "CLAVE_DUPLICADA",
                  "detalle", "La clave de acceso ya existe en otra retención autorizada o enviada.",
                  "claveAcceso", claveAcceso);
         }
      }

      return null;
   }

   private AutorizacionResultado enviarFirmarYAutorizar(String xmlPlano) {
      String url = sriMicroserviceBaseUrl + "/api/singsend/retencion/string?modo=XADES_BES&download=false";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);
      HttpEntity<String> request = new HttpEntity<>(xmlPlano, headers);
      try {
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
         String body = response.getBody() == null ? "" : response.getBody().trim();

         if (response.getStatusCode().is2xxSuccessful() && body.startsWith("<")) {
            return AutorizacionResultado.autorizado(body);
         }

         if (response.getStatusCode().value() == 202 || contienePendiente(body)) {
            return AutorizacionResultado.pendiente(
                  "La autorizacion aun no esta disponible",
                  response.getStatusCode().value(),
                  body);
         }

         if (response.getStatusCode().value() == 502 && contieneAutorizadoSinXml(body)) {
            return AutorizacionResultado.pendiente(
                  "El SRI ya respondio la autorizacion, pero todavia no entrega el XML autorizado",
                  response.getStatusCode().value(),
                  body);
         }

         throw new IllegalStateException("Respuesta inesperada del servicio SRI (" + response.getStatusCode() + "): "
               + body);
      } catch (HttpStatusCodeException ex) {
         String responseBody = ex.getResponseBodyAsString();
         if (ex.getStatusCode().value() == 502 && contieneAutorizadoSinXml(responseBody)) {
            return AutorizacionResultado.pendiente(
                  "El microservicio SRI devolvio 502 porque la autorizacion todavia no contiene XML autorizado",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         if (ex.getStatusCode().value() == 400 && contieneLimiteDiario(responseBody)) {
            return AutorizacionResultado.limiteDiario(
                  "El SRI ya alcanzó el límite diario de intentos para esta retención. Intente nuevamente el siguiente día.",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         throw new IllegalStateException(
               "El microservicio SRI devolvio " + ex.getStatusCode().value() + ": "
                     + (responseBody == null || responseBody.isBlank() ? ex.getMessage() : responseBody),
               ex);
      }
   }

   private boolean contienePendiente(String value) {
      return contieneTexto(value, "pendiente");
   }

   private boolean contieneAutorizadoSinXml(String value) {
      return contieneTexto(value, "autorizado_sin_xml")
            || contieneTexto(value, "sin <comprobante>")
            || contieneTexto(value, "sin comprobante")
            || contieneTexto(value, "autorizacion no devolvio xml autorizado")
            || contieneTexto(value, "autorizacion no devolvio xml autorizado");
   }

   private boolean contieneLimiteDiario(String value) {
      return contieneTexto(value, "limite de intentos no autorizados por dia")
            || contieneTexto(value, "límite de intentos no autorizados por día")
            || contieneTexto(value, "superado el limite diario")
            || contieneTexto(value, "superado el límite diario");
   }

   private boolean contieneTexto(String value, String needle) {
      if (!StringUtils.hasText(value) || !StringUtils.hasText(needle)) {
         return false;
      }
      return value.toLowerCase(Locale.ROOT).contains(needle.toLowerCase(Locale.ROOT));
   }

   private void aplicarAutorizacion(Retenciones retencion, String xmlAutorizado, String numeroAutorizacion,
         String fechaAutorizacion) {
      String numero = firstNonBlank(numeroAutorizacion, retencion.getNumautoriza_e(), retencion.getNumautoriza());
      String claveAcceso = firstNonBlank(retencion.getClaveacceso(), retencion.getNumautoriza(), numero);
      String fecha = normalizarFechaAutorizacion(fechaAutorizacion);

      retencion.setClaveacceso(claveAcceso);
      retencion.setNumautoriza(numero);
      retencion.setNumautoriza_e(numero);
      retencion.setFecautoriza(fecha);
      retencion.setAutorizacion(xmlAutorizado);
   }

   private String extraerTag(String xml, String tagName) {
      try {
         DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         dbf.setNamespaceAware(true);
         Document doc = dbf.newDocumentBuilder()
               .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
         NodeList nodes = doc.getElementsByTagName(tagName);
         if (nodes == null || nodes.getLength() == 0) {
            return "";
         }
         return nodes.item(0).getTextContent().trim();
      } catch (Exception e) {
         return "";
      }
   }

   private String normalizarFechaAutorizacion(String fecha) {
      if (!StringUtils.hasText(fecha)) {
         return null;
      }
      String value = fecha.trim();
      try {
         return OffsetDateTime.parse(value).toLocalDate().toString();
      } catch (Exception ignore) {
      }
      try {
         return LocalDate.parse(value, DateTimeFormatter.ISO_DATE).toString();
      } catch (Exception ignore) {
      }
      return value;
   }

   private String safeValue(Object value, String fallback) {
      if (value == null) {
         return fallback;
      }
      String text = String.valueOf(value).trim();
      return text.isBlank() ? fallback : text;
   }

   private String firstNonBlank(String... values) {
      if (values == null) {
         return "";
      }
      for (String value : values) {
         if (StringUtils.hasText(value)) {
            return value.trim();
         }
      }
      return "";
   }

   private static final class AutorizacionResultado {
      private final boolean pendiente;
      private final boolean limiteDiario;
      private final String xmlAutorizado;
      private final String mensaje;
      private final int codigoHttp;
      private final String detalle;

      private AutorizacionResultado(boolean pendiente, boolean limiteDiario, String xmlAutorizado, String mensaje, int codigoHttp,
            String detalle) {
         this.pendiente = pendiente;
         this.limiteDiario = limiteDiario;
         this.xmlAutorizado = xmlAutorizado;
         this.mensaje = mensaje;
         this.codigoHttp = codigoHttp;
         this.detalle = detalle;
      }

      private static AutorizacionResultado autorizado(String xmlAutorizado) {
         return new AutorizacionResultado(false, false, xmlAutorizado, null, 200, null);
      }

      private static AutorizacionResultado pendiente(String mensaje, int codigoHttp, String detalle) {
         return new AutorizacionResultado(true, false, null, mensaje, codigoHttp, detalle);
      }

      private static AutorizacionResultado limiteDiario(String mensaje, int codigoHttp, String detalle) {
         return new AutorizacionResultado(false, true, null, mensaje, codigoHttp, detalle);
      }
   }
}
