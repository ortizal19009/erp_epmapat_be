package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
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
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

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
   private final ObjectMapper objectMapper;
   private final RetencionSseService retencionSseService;

   @Value("${sri.microservice.base-url:http://192.168.0.33:9096}")
   private String sriMicroserviceBaseUrl;

   public RetencionProcesamientoSRIService(
         RetencionSRIService retencionSRIService,
         RetencionPdfService retencionPdfService,
         RetencionEmailService retencionEmailService,
         RetencionesServicio retencionesServicio,
         Fec_retencionesServicio fecRetencionesServicio,
         RetencionesR retencionesR,
         RestTemplate restTemplate,
         CorreosEnviadosServicio correosEnviadosServicio,
         ObjectMapper objectMapper,
         RetencionSseService retencionSseService) {
      this.retencionSRIService = retencionSRIService;
      this.retencionPdfService = retencionPdfService;
      this.retencionEmailService = retencionEmailService;
      this.retencionesServicio = retencionesServicio;
      this.fecRetencionesServicio = fecRetencionesServicio;
      this.retencionesR = retencionesR;
      this.restTemplate = restTemplate;
      this.correosEnviadosServicio = correosEnviadosServicio;
      this.objectMapper = objectMapper;
      this.retencionSseService = retencionSseService;
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
         Map<String, Object> response = Map.of(
               "idretencion", idretencion,
               "estado", "LIMITE_INTENTOS_DIARIO",
               "detalle", resultado.mensaje,
               "xmlautorizado", "",
               "errores", resultado.detalle);
         publicarActualizacion(response);
         return response;
      }
      if (resultado.noAutorizado) {
         retencionSRIService.actualizarEstado(idretencion, "NO_AUTORIZADO", resultado.mensaje);
         Map<String, Object> response = construirRespuestaNoAutorizada(idretencion, resultado);
         publicarActualizacion(response);
         return response;
      }
      if (resultado.pendiente) {
         retencionSRIService.actualizarEstado(idretencion, "PENDIENTE_AUTORIZACION", resultado.mensaje);
         Map<String, Object> response = Map.of(
               "idretencion", idretencion,
               "estado", "PENDIENTE_AUTORIZACION",
               "detalle", resultado.mensaje,
               "xmlautorizado", "");
         publicarActualizacion(response);
         return response;
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

      Map<String, Object> response = Map.of(
            "idretencion", idretencion,
            "estado", "AUTORIZADA",
            "numeroAutorizacion", numeroAutorizacion,
            "fechaAutorizacion", fechaAutorizacion,
            "xmlautorizado", xmlAutorizado,
            "email", safeValue(mail.get("email"), destinatario));
      publicarActualizacion(response);
      return response;
   }

   @Transactional
   public Map<String, Object> consultarEstadoPendiente(Long idretencion) {
      Retenciones retencion = retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retencion " + idretencion));

      String claveAcceso = firstNonBlank(retencion.getClaveacceso(), retencion.getNumautoriza(), retencion.getNumautoriza_e());
      if (!StringUtils.hasText(claveAcceso)) {
         throw new IllegalStateException("La retencion " + idretencion + " no tiene clave de acceso para consultar.");
      }

      AutorizacionResultado resultado = consultarAutorizacionPorClave(claveAcceso);
      if (resultado.noAutorizado) {
         retencionSRIService.actualizarEstado(idretencion, "NO_AUTORIZADO", resultado.mensaje);
         Map<String, Object> response = construirRespuestaNoAutorizada(idretencion, resultado);
         publicarActualizacion(response);
         return response;
      }
      if (resultado.pendiente) {
         retencionSRIService.actualizarEstado(idretencion, "PENDIENTE_AUTORIZACION", resultado.mensaje);
         Map<String, Object> response = Map.of(
               "idretencion", idretencion,
               "estado", "PENDIENTE_AUTORIZACION",
               "detalle", resultado.mensaje,
               "claveAcceso", claveAcceso,
               "xmlautorizado", "",
               "errores", safeValue(resultado.detalle, ""));
         publicarActualizacion(response);
         return response;
      }

      String xmlAutorizado = resultado.xmlAutorizado;
      if (!StringUtils.hasText(xmlAutorizado)) {
         throw new IllegalStateException("La consulta del SRI no devolvio XML autorizado para la retencion " + idretencion);
      }

      String numeroAutorizacion = firstNonBlank(
            extraerTag(xmlAutorizado, "numeroAutorizacion"),
            resultado.numeroAutorizacion);
      String fechaAutorizacion = firstNonBlank(
            extraerTag(xmlAutorizado, "fechaAutorizacion"),
            resultado.fechaAutorizacion);

      aplicarAutorizacion(retencion, xmlAutorizado, numeroAutorizacion, fechaAutorizacion);
      retencion.setEstado(1);
      retencionesServicio.updateRetencion(idretencion, retencion);
      retencionSRIService.actualizarXmlAutorizado(idretencion, xmlAutorizado, "AUTORIZADA", null);

      Map<String, Object> response = Map.of(
            "idretencion", idretencion,
            "estado", "AUTORIZADA",
            "claveAcceso", claveAcceso,
            "numeroAutorizacion", safeValue(numeroAutorizacion, ""),
            "fechaAutorizacion", safeValue(fechaAutorizacion, ""),
            "xmlautorizado", xmlAutorizado);
      publicarActualizacion(response);
      return response;
   }

   private Map<String, Object> validarRetencionYaProcesada(Retenciones retencion) {
      String claveAcceso = firstNonBlank(retencion.getClaveacceso(), "");
      String numeroAutorizacion = firstNonBlank(retencion.getNumautoriza_e(), retencion.getNumautoriza(), "");
      Integer estado = retencion.getEstado();

      if (estado != null && estado == 1 && StringUtils.hasText(numeroAutorizacion)) {
         return Map.of(
               "idretencion", retencion.getIdrete(),
               "estado", "YA_AUTORIZADA",
               "detalle", "La retencion ya fue enviada/autorizada previamente.",
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
                  "detalle", "La clave de acceso ya existe en otra retencion autorizada o enviada.",
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
                  "La retencion sigue pendiente de autorizacion: el SRI aun no devuelve el XML autorizado. Intente consultarla nuevamente en unos minutos.",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         if (ex.getStatusCode().value() == 400 && contieneLimiteDiario(responseBody)) {
            return AutorizacionResultado.limiteDiario(
                  "El SRI ya alcanzo el limite diario de intentos para esta retencion. Intente nuevamente el siguiente dia.",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         if (ex.getStatusCode().value() == 400 && contieneNoAutorizado(responseBody)) {
            return AutorizacionResultado.noAutorizado(
                  construirMensajeNoAutorizado(responseBody),
                  ex.getStatusCode().value(),
                  responseBody,
                  extraerCampoJson(responseBody, "claveAcceso"),
                  extraerCampoJson(responseBody, "numeroAutorizacion"),
                  extraerCampoJson(responseBody, "fechaAutorizacion"));
         }
         throw new IllegalStateException(
               "El microservicio SRI devolvio " + ex.getStatusCode().value() + ": "
                     + (responseBody == null || responseBody.isBlank() ? ex.getMessage() : responseBody),
               ex);
      }
   }

   private AutorizacionResultado consultarAutorizacionPorClave(String claveAcceso) {
      String url = sriMicroserviceBaseUrl + "/api/singsend/retenciones/xml?claveAcceso=" + claveAcceso;
      try {
         ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, HttpEntity.EMPTY, String.class);
         String body = response.getBody() == null ? "" : response.getBody().trim();

         if (response.getStatusCode().is2xxSuccessful() && body.startsWith("<")) {
            return AutorizacionResultado.autorizado(body);
         }
         if (response.getStatusCode().value() == 202 || contienePendiente(body)) {
            return AutorizacionResultado.pendiente(
                  "La autorizacion aun sigue pendiente en el SRI",
                  response.getStatusCode().value(),
                  body);
         }
         if (response.getStatusCode().value() == 502 && contieneAutorizadoSinXml(body)) {
            return AutorizacionResultado.pendiente(
                  "El SRI reporta autorizacion, pero todavia no entrega el XML autorizado",
                  response.getStatusCode().value(),
                  body);
         }

         throw new IllegalStateException("Respuesta inesperada al consultar el SRI (" + response.getStatusCode() + "): " + body);
      } catch (HttpStatusCodeException ex) {
         String responseBody = ex.getResponseBodyAsString();
         if (ex.getStatusCode().value() == 502 && contieneAutorizadoSinXml(responseBody)) {
            return AutorizacionResultado.pendiente(
                  "La retencion sigue pendiente de autorizacion: el SRI aun no devuelve el XML autorizado. Intente consultarla nuevamente en unos minutos.",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         if (ex.getStatusCode().value() == 400 && contieneNoAutorizado(responseBody)) {
            return AutorizacionResultado.noAutorizado(
                  construirMensajeNoAutorizado(responseBody),
                  ex.getStatusCode().value(),
                  responseBody,
                  firstNonBlank(extraerCampoJson(responseBody, "claveAcceso"), claveAcceso),
                  extraerCampoJson(responseBody, "numeroAutorizacion"),
                  extraerCampoJson(responseBody, "fechaAutorizacion"));
         }
         if (ex.getStatusCode().value() == 202 || contienePendiente(responseBody)) {
            return AutorizacionResultado.pendiente(
                  "La autorizacion aun sigue pendiente en el SRI",
                  ex.getStatusCode().value(),
                  responseBody);
         }
         throw new IllegalStateException(
               "El microservicio SRI devolvio " + ex.getStatusCode().value() + " al consultar clave "
                     + claveAcceso + ": " + (responseBody == null || responseBody.isBlank() ? ex.getMessage() : responseBody),
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
            || contieneTexto(value, "autorizacion no devolvio xml autorizado");
   }

   private boolean contieneLimiteDiario(String value) {
      return contieneTexto(value, "limite de intentos no autorizados por dia")
            || contieneTexto(value, "limite de intentos no autorizados por dia")
            || contieneTexto(value, "superado el limite diario");
   }

   private boolean contieneNoAutorizado(String value) {
      return contieneTexto(value, "\"estadoAutorizacion\":\"NO AUTORIZADO\"")
            || contieneTexto(value, "\"estadoautorizacion\":\"no autorizado\"")
            || contieneTexto(value, "no autorizado");
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
      // El XML autorizado completo se conserva en fec_retenciones.xmlautorizado.
      // En retenciones solo dejamos metadatos cortos para evitar desbordar columnas legacy.
      retencion.setAutorizacion(null);
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

   private Map<String, Object> construirRespuestaNoAutorizada(Long idretencion, AutorizacionResultado resultado) {
      Map<String, Object> respuesta = new LinkedHashMap<>();
      respuesta.put("idretencion", idretencion);
      respuesta.put("estado", "NO_AUTORIZADO");
      respuesta.put("detalle", resultado.mensaje);
      respuesta.put("claveAcceso", safeValue(resultado.claveAcceso, ""));
      respuesta.put("numeroAutorizacion", safeValue(resultado.numeroAutorizacion, ""));
      respuesta.put("fechaAutorizacion", safeValue(resultado.fechaAutorizacion, ""));
      respuesta.put("xmlautorizado", "");
      respuesta.put("errores", resultado.detalle);
      return respuesta;
   }

   private String construirMensajeNoAutorizado(String responseBody) {
      String ambiente = extraerCampoJson(responseBody, "ambiente");
      String claveAcceso = extraerCampoJson(responseBody, "claveAcceso");
      String base = "El SRI devolvio la retencion como NO AUTORIZADO.";
      if (StringUtils.hasText(ambiente)) {
         base += " Ambiente: " + ambiente + ".";
      }
      if (StringUtils.hasText(claveAcceso)) {
         base += " Clave de acceso: " + claveAcceso + ".";
      }
      return base;
   }

   private String extraerCampoJson(String responseBody, String fieldName) {
      if (!StringUtils.hasText(responseBody) || !StringUtils.hasText(fieldName)) {
         return "";
      }
      try {
         Map<String, Object> payload = objectMapper.readValue(responseBody, new TypeReference<Map<String, Object>>() {
         });
         Object value = payload.get(fieldName);
         return value == null ? "" : String.valueOf(value).trim();
      } catch (Exception e) {
         return "";
      }
   }

   private void publicarActualizacion(Map<String, Object> payload) {
      retencionSseService.publishEstadoActualizado(payload);
   }

   private static final class AutorizacionResultado {
      private final boolean pendiente;
      private final boolean limiteDiario;
      private final boolean noAutorizado;
      private final String xmlAutorizado;
      private final String mensaje;
      private final int codigoHttp;
      private final String detalle;
      private final String claveAcceso;
      private final String numeroAutorizacion;
      private final String fechaAutorizacion;

      private AutorizacionResultado(boolean pendiente, boolean limiteDiario, boolean noAutorizado, String xmlAutorizado,
            String mensaje, int codigoHttp, String detalle, String claveAcceso, String numeroAutorizacion,
            String fechaAutorizacion) {
         this.pendiente = pendiente;
         this.limiteDiario = limiteDiario;
         this.noAutorizado = noAutorizado;
         this.xmlAutorizado = xmlAutorizado;
         this.mensaje = mensaje;
         this.codigoHttp = codigoHttp;
         this.detalle = detalle;
         this.claveAcceso = claveAcceso;
         this.numeroAutorizacion = numeroAutorizacion;
         this.fechaAutorizacion = fechaAutorizacion;
      }

      private static AutorizacionResultado autorizado(String xmlAutorizado) {
         return new AutorizacionResultado(false, false, false, xmlAutorizado, null, 200, null, "", "", "");
      }

      private static AutorizacionResultado pendiente(String mensaje, int codigoHttp, String detalle) {
         return new AutorizacionResultado(true, false, false, null, mensaje, codigoHttp, detalle, "", "", "");
      }

      private static AutorizacionResultado limiteDiario(String mensaje, int codigoHttp, String detalle) {
         return new AutorizacionResultado(false, true, false, null, mensaje, codigoHttp, detalle, "", "", "");
      }

      private static AutorizacionResultado noAutorizado(String mensaje, int codigoHttp, String detalle, String claveAcceso,
            String numeroAutorizacion, String fechaAutorizacion) {
         return new AutorizacionResultado(false, false, true, null, mensaje, codigoHttp, detalle, claveAcceso,
               numeroAutorizacion, fechaAutorizacion);
      }
   }
}
