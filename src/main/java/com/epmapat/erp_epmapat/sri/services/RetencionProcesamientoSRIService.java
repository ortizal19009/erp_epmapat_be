package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
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
import org.springframework.web.client.RestTemplate;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;

@Service
public class RetencionProcesamientoSRIService {

   private final RetencionSRIService retencionSRIService;
   private final RetencionPdfService retencionPdfService;
   private final RetencionEmailService retencionEmailService;
   private final RetencionesServicio retencionesServicio;
   private final RetencionesR retencionesR;
   private final RestTemplate restTemplate;
   private final CorreosEnviadosServicio correosEnviadosServicio;

   @Value("${sri.microservice.base-url:http://localhost:8080}")
   private String sriMicroserviceBaseUrl;

   public RetencionProcesamientoSRIService(
         RetencionSRIService retencionSRIService,
         RetencionPdfService retencionPdfService,
         RetencionEmailService retencionEmailService,
         RetencionesServicio retencionesServicio,
         RetencionesR retencionesR,
         RestTemplate restTemplate,
         CorreosEnviadosServicio correosEnviadosServicio) {
      this.retencionSRIService = retencionSRIService;
      this.retencionPdfService = retencionPdfService;
      this.retencionEmailService = retencionEmailService;
      this.retencionesServicio = retencionesServicio;
      this.retencionesR = retencionesR;
      this.restTemplate = restTemplate;
      this.correosEnviadosServicio = correosEnviadosServicio;
   }

   @Transactional
   public Map<String, Object> procesar(Long idretencion, String destinatario, String asunto, String mensaje) {
      Retenciones retencion = retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retención " + idretencion));

      var generada = retencionSRIService.generarYGuardar(idretencion);
      String xmlPlano = generada.getXmlautorizado();
      if (!StringUtils.hasText(xmlPlano)) {
         throw new IllegalStateException("No se pudo generar el XML de la retención " + idretencion);
      }

      String xmlAutorizado = enviarFirmarYAutorizar(xmlPlano);
      if (!StringUtils.hasText(xmlAutorizado)) {
         throw new IllegalStateException("El SRI no devolvió una autorización para la retención " + idretencion);
      }

      String numeroAutorizacion = extraerTag(xmlAutorizado, "numeroAutorizacion");
      String fechaAutorizacion = extraerTag(xmlAutorizado, "fechaAutorizacion");

      retencion.setNumautoriza_e(numeroAutorizacion);
      retencion.setFecautoriza(normalizarFechaAutorizacion(fechaAutorizacion));
      retencion.setAutorizacion(xmlAutorizado);
      retencionesServicio.save(retencion);

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
            safeValue(asunto, "Comprobante de retención"),
            "sri",
            safeValue(mail.get("archivo"), "retencion.pdf"),
            "ENVIADO",
            "Retención autorizada y enviada por correo");

      return Map.of(
            "idretencion", idretencion,
            "estado", "AUTORIZADA",
            "numeroAutorizacion", numeroAutorizacion,
            "fechaAutorizacion", fechaAutorizacion,
            "xmlautorizado", xmlAutorizado,
            "email", safeValue(mail.get("email"), destinatario));
   }

   private String enviarFirmarYAutorizar(String xmlPlano) {
      String url = sriMicroserviceBaseUrl + "/api/singsend/retencion/string?modo=XADES_BES&download=false";
      HttpHeaders headers = new HttpHeaders();
      headers.setContentType(MediaType.APPLICATION_XML);
      HttpEntity<String> request = new HttpEntity<>(xmlPlano, headers);

      ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, request, String.class);
      String body = response.getBody() == null ? "" : response.getBody().trim();
      if (response.getStatusCode().is2xxSuccessful() && body.startsWith("<")) {
         return body;
      }
      if (response.getStatusCode().value() == 202 || body.contains("\"PENDIENTE\"")) {
         throw new IllegalStateException("La autorización aún no está disponible");
      }
      throw new IllegalStateException("Respuesta inesperada del servicio SRI (" + response.getStatusCode() + "): " + body);
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
}
