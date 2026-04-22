package com.epmapat.erp_epmapat.sri.services;

import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.modelo.contabilidad.Beneficiarios;
import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.servicio.administracion.CorreosEnviadosServicio;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;

@Service
public class RetencionEmailService {

   @Autowired
   private RetencionesR retencionesR;
   @Autowired
   private RetencionSRIService retencionSRIService;
   @Autowired
   private RetencionPdfService retencionPdfService;
   @Autowired
   private EmailService emailService;
   @Autowired
   private DefinirServicio definirServicio;
   @Autowired
   private CorreosEnviadosServicio correosEnviadosServicio;

   public Map<String, Object> enviarRetencion(Long idretencion, String destinatario, String asunto, String mensaje) {
      return enviarRetencion(idretencion, destinatario, asunto, mensaje, true);
   }

   public Map<String, Object> enviarRetencion(Long idretencion, String destinatario, String asunto, String mensaje,
         boolean actualizarEstado) {
      Retenciones retencion = retencionesR.findById(idretencion)
            .orElseThrow(() -> new IllegalArgumentException("No existe la retención " + idretencion));
      Fec_retenciones generada = actualizarEstado
            ? retencionSRIService.generarYGuardar(idretencion)
            : retencionSRIService.actualizarXmlAutorizado(idretencion, retencion.getAutorizacion(), "AUTORIZADA", null);
      Definir definir = definirServicio.findById(1L)
            .orElseThrow(() -> new IllegalArgumentException("No existe la configuración general para correo"));

      List<String> correosDestino = normalizarDestinatarios(destinatario, generada.getEmailsujetoretenido(),
            obtenerEmailBeneficiario(retencion));
      if (correosDestino.isEmpty()) {
         throw new IllegalArgumentException("No existe un correo destino para la retención " + idretencion);
      }

      byte[] pdfBytes = retencionPdfService.generarPdf(idretencion).toByteArray();
      InMemoryMultipartFile pdf = new InMemoryMultipartFile(
            "retencion_pdf",
            construirNombreArchivo(retencion, "pdf"),
            "application/pdf",
            pdfBytes);

      String emisor = valueOf(definir.getEmail());
      String password = "";
      try {
         Object clave = definirServicio.desEncriptar(1L);
         password = clave == null ? "" : String.valueOf(clave);
      } catch (Exception ex) {
         throw new IllegalStateException("No se pudo desencriptar la clave de correo: " + ex.getMessage(), ex);
      }

      String cuerpo = mensaje == null || mensaje.isBlank()
            ? "Adjuntamos su comprobante de retención en formato PDF."
            : mensaje;
      String correoConcatenado = String.join("; ", correosDestino);
      boolean enviado = emailService.envioEmail(
            emisor,
            password,
            correosDestino,
            asunto == null || asunto.isBlank() ? "Comprobante de retención" : asunto,
            cuerpo,
            pdf);

      if (!enviado) {
         correosEnviadosServicio.registrarEnvio(
               "RETENCIONES",
               idretencion,
               "RETENCION",
               correoConcatenado,
               asunto == null || asunto.isBlank() ? "Comprobante de retención" : asunto,
               emisor,
               construirNombreArchivo(retencion, "pdf"),
               "ERROR",
               "No se pudo enviar el correo");
         retencionSRIService.actualizarEstado(idretencion, "ERROR_ENVIO", "No se pudo enviar el correo");
         throw new IllegalStateException("No se pudo enviar el correo de la retención " + idretencion);
      }

      correosEnviadosServicio.registrarEnvio(
            "RETENCIONES",
            idretencion,
            "RETENCION",
            correoConcatenado,
            asunto == null || asunto.isBlank() ? "Comprobante de retención" : asunto,
            emisor,
            construirNombreArchivo(retencion, "pdf"),
            "ENVIADO",
            "Correo enviado correctamente");
      Fec_retenciones actualizado = actualizarEstado
            ? retencionSRIService.actualizarEstado(idretencion, "ENVIADA", null, correoConcatenado)
            : retencionSRIService.actualizarXmlAutorizado(idretencion, retencion.getAutorizacion(), "AUTORIZADA", null);
      return Map.of(
            "idretencion", actualizado.getIdretencion(),
            "estado", actualizado.getEstado(),
            "email", correoConcatenado,
            "archivo", construirNombreArchivo(retencion, "pdf"));
   }

   private String obtenerEmailBeneficiario(Retenciones retencion) {
      Beneficiarios beneficiario = retencion.getIdbene();
      if (beneficiario == null || beneficiario.getMailben() == null) {
         return "";
      }
      return beneficiario.getMailben().trim();
   }

   private String construirNombreArchivo(Retenciones retencion, String extension) {
      String secuencial = retencion.getSecretencion1() == null ? String.valueOf(retencion.getIdrete())
            : retencion.getSecretencion1().replaceAll("[^0-9]", "");
      if (secuencial.isBlank()) {
         secuencial = String.valueOf(retencion.getIdrete());
      }
      return "retencion_" + secuencial + "." + extension;
   }

   private String firstNonBlank(String... values) {
      if (values == null) {
         return "";
      }
      for (String value : values) {
         if (value != null && !value.trim().isEmpty()) {
            return value.trim();
         }
      }
      return "";
   }

   private List<String> normalizarDestinatarios(String... values) {
      List<String> resultado = new java.util.ArrayList<>();
      if (values == null) {
         return resultado;
      }
      Pattern separador = Pattern.compile("[;\\s]+");
      for (String value : values) {
         if (value == null || value.trim().isEmpty()) {
            continue;
         }
         String[] partes = separador.split(value.trim());
         for (String parte : partes) {
            if (parte != null && !parte.trim().isEmpty()) {
               resultado.add(parte.trim());
            }
         }
      }
      return resultado.stream().distinct().toList();
   }

   private String valueOf(Object value) {
      return value == null ? "" : String.valueOf(value);
   }
}
