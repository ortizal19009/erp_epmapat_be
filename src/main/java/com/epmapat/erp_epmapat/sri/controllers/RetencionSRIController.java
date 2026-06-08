package com.epmapat.erp_epmapat.sri.controllers;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.sri.dtos.ClaveAccesoValidationResult;
import com.epmapat.erp_epmapat.sri.services.RetencionClaveAccesoService;
import com.epmapat.erp_epmapat.sri.services.RetencionProcesamientoSRIService;
import com.epmapat.erp_epmapat.sri.services.RetencionEmailService;
import com.epmapat.erp_epmapat.sri.services.RetencionPdfService;
import com.epmapat.erp_epmapat.sri.services.RetencionSRIService;
import com.epmapat.erp_epmapat.sri.services.RetencionSseService;

@RestController
@CrossOrigin(originPatterns = "*")
@RequestMapping("/api/sri/retenciones")
public class RetencionSRIController {

   @Autowired
   private RetencionSRIService retencionSRIService;
   @Autowired
   private RetencionClaveAccesoService retencionClaveAccesoService;
   @Autowired
   private RetencionPdfService retencionPdfService;
   @Autowired
   private RetencionEmailService retencionEmailService;
   @Autowired
   private RetencionProcesamientoSRIService retencionProcesamientoSRIService;
   @Autowired
   private RetencionSseService retencionSseService;

   @GetMapping
   public List<Fec_retenciones> listar(@RequestParam(required = false) String estado) {
      if (estado == null || estado.isBlank()) {
         return retencionSRIService.listarTodas();
      }
      return retencionSRIService.listarPorEstado(estado);
   }

   @GetMapping("/xml")
   public ResponseEntity<String> generarXml(@RequestParam Long idretencion) {
      String xml = retencionSRIService.generarXml(idretencion);
      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=retencion.xml")
            .contentType(MediaType.APPLICATION_XML)
            .body(xml);
   }

   @GetMapping(path = "/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
   public SseEmitter streamRetenciones() {
      return retencionSseService.subscribe();
   }

   @Transactional
   @PostMapping("/xml")
   public ResponseEntity<Map<String, Object>> generarYGuardarXml(@RequestParam Long idretencion) {
      Fec_retenciones guardado = retencionSRIService.generarYGuardar(idretencion);
      return ResponseEntity.ok(Map.of(
            "idretencion", guardado.getIdretencion(),
            "estado", guardado.getEstado(),
            "xmlautorizado", guardado.getXmlautorizado()));
   }

   @GetMapping("/pdf")
   public ResponseEntity<byte[]> generarPdf(@RequestParam Long idretencion) {
      ByteArrayOutputStream pdf = retencionPdfService.generarPdf(idretencion);
      byte[] bytes = pdf.toByteArray();
      return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=retencion_" + idretencion + ".pdf")
            .contentType(MediaType.APPLICATION_PDF)
            .contentLength(bytes.length)
            .body(bytes);
   }

   @PostMapping("/procesar")
   public ResponseEntity<Map<String, Object>> procesarRetencion(
         @RequestParam Long idretencion,
         @RequestParam(required = false) String destinatario,
         @RequestParam(required = false) String asunto,
         @RequestParam(required = false) String mensaje) {
      Map<String, Object> resultado = retencionProcesamientoSRIService.procesar(idretencion, destinatario, asunto,
            mensaje);
      return ResponseEntity.ok(resultado);
   }

   @PostMapping("/consultar")
   public ResponseEntity<Map<String, Object>> consultarRetencionPendiente(@RequestParam Long idretencion) {
      Map<String, Object> resultado = retencionProcesamientoSRIService.consultarEstadoPendiente(idretencion);
      return ResponseEntity.ok(resultado);
   }

   @PostMapping("/email")
   public ResponseEntity<Map<String, Object>> enviarCorreo(
         @RequestParam Long idretencion,
         @RequestParam(required = false) String destinatario,
         @RequestParam(required = false) String asunto,
         @RequestParam(required = false) String mensaje) {
      Map<String, Object> resultado = retencionEmailService.enviarRetencion(idretencion, destinatario, asunto, mensaje);
      return ResponseEntity.ok(resultado);
   }

   @GetMapping("/estado/{estado}")
   public List<Fec_retenciones> listarPorEstado(@PathVariable String estado) {
      return retencionSRIService.listarPorEstado(estado);
   }

   @PostMapping("/{idretencion}/estado")
   public ResponseEntity<Fec_retenciones> actualizarEstado(
         @PathVariable Long idretencion,
         @RequestParam String estado,
         @RequestParam(required = false) String errores) {
      Fec_retenciones actualizado = retencionSRIService.actualizarEstado(idretencion, estado, errores);
      return ResponseEntity.ok(actualizado);
   }

   @GetMapping("/clave-acceso/validar")
   public ResponseEntity<ClaveAccesoValidationResult> validarClaveAcceso(@RequestParam String claveAcceso) {
      return ResponseEntity.ok(retencionClaveAccesoService.validarClaveAcceso(claveAcceso));
   }

   @PostMapping("/{idretencion}/clave-acceso/regenerar")
   public ResponseEntity<Map<String, Object>> regenerarClaveAcceso(@PathVariable Long idretencion) {
      Retenciones retencion = retencionClaveAccesoService.regenerarClaveAcceso(idretencion);
      Fec_retenciones guardado = retencionSRIService.generarYGuardar(idretencion);
      return ResponseEntity.ok(Map.of(
            "idretencion", idretencion,
            "claveAcceso", retencion.getClaveacceso(),
            "estado", guardado.getEstado()));
   }
}
