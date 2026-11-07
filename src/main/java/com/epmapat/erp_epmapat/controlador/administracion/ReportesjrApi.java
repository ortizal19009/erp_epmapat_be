package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.epmapat.erp_epmapat.modelo.administracion.Reportesjr;
import com.epmapat.erp_epmapat.servicio.administracion.ReportejrService;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reportesjr")
@CrossOrigin("*")

public class ReportesjrApi {

   private final ReportejrService repojrService;

   // Buscar reportesjr por nombre y desrep ordenados por codrep
   @GetMapping("/busca")
   public ResponseEntity<List<Reportesjr>> buscarPorOpcionNomrepYDesrep(@RequestParam("codigo") String codigo,
         @RequestParam("nomrep") String nomrep,
         @RequestParam("desrep") String desrep,
         @RequestParam(value = "opciones", required = false) java.util.Set<Short> opciones) {
      List<Reportesjr> resultados = repojrService.buscarPorOpcionNomrepYDesrep(codigo, nomrep, desrep);
      return ResponseEntity.ok(resultados);
   }

   // Valida nomrep
   @GetMapping("/valnomrep/{nomrep}")
   public ResponseEntity<Boolean> valNomrep(@PathVariable String nomrep) {
      boolean existe = repojrService.valNomrep(nomrep);
      return ResponseEntity.ok(existe);
   }

   // Reporte por id
   @GetMapping("/{id}")
   public ResponseEntity<Reportesjr> obtenerPorId(@PathVariable Short id) {
      return repojrService.buscaPorId(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
   }

   // Reporte por nombre
   @GetMapping("/nomrep/{nomrep}")
   public ResponseEntity<Reportesjr> buscarPorNombre(@PathVariable("nomrep") String nomrep) {
      Reportesjr reporte = repojrService.findByNomrep(nomrep);
      if (reporte != null) {
         return ResponseEntity.ok(reporte);
      } else {
         return ResponseEntity.notFound().build();
      }
   }

   // Buscar todos los reportes con un Repoxopcion_Codigo específico
   @GetMapping("/codigo/{codigo}")
   public ResponseEntity<List<Reportesjr>> obtenerPorCodigo(@PathVariable String codigo) {
      List<Reportesjr> reportes = repojrService.buscaPorRepoxopcion_CodigoOrderByNomrep(codigo);
      return ResponseEntity.ok(reportes);
   }

   // Conteo por idrepoxopcion
   @GetMapping("/countidrepoxopcion/{idrepoxopcion}")
   public ResponseEntity<Short> cuentaPorRepoxopcion(@PathVariable short idrepoxopcion) {
      short cantidad = repojrService.cuentaPorRepoxopcion(idrepoxopcion);
      return ResponseEntity.ok(cantidad);
   }

   // Nuevo reporte
   @PostMapping(consumes = "multipart/form-data")
   public ResponseEntity<Reportesjr> crearReporte(
         @RequestParam("idrepoxopcion") Short idrepoxopcion,
         @RequestParam("nomrep") String nomrep,
         @RequestParam("metodo") Short metodo,
         @RequestParam("desrep") String desrep,
         @RequestParam("jrxml") MultipartFile jrxml,
         @RequestParam("jasper") MultipartFile jasper,
         @RequestParam(value = "opciones", required = false) java.util.Set<Short> opciones) {
      try {
         Reportesjr nuevo = repojrService.crearReporte(idrepoxopcion, nomrep, metodo, desrep, jrxml, jasper, opciones);
         return ResponseEntity.ok(nuevo);
      } catch (IllegalArgumentException e) {
         throw e;
      } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.badRequest().build();
      }
   }

   // Para actualizar solo metadatos (sin archivos)
   @PutMapping("/{id}/metadatos")
   public ResponseEntity<Reportesjr> actualizarMetadatos(@PathVariable Short id,
         @RequestParam("idrepoxopcion") Short idrepoxopcion,
         @RequestParam("nomrep") String nomrep,
         @RequestParam("metodo") Short metodo,
         @RequestParam("desrep") String desrep,
         @RequestParam(value = "opciones", required = false) java.util.Set<Short> opciones) {

      Reportesjr actualizado = repojrService.actualizarSoloCampos(id, idrepoxopcion, nomrep, metodo, desrep, opciones);
      return ResponseEntity.ok(actualizado);
   }

   // Actualizar todo (metadatos y archivos)
   @PutMapping(value = "/{id}", consumes = "multipart/form-data")
   public ResponseEntity<Reportesjr> actualizarCompleto(@PathVariable Short id,
         @RequestParam("idrepoxopcion") Short idrepoxopcion,
         @RequestParam("nomrep") String nomrep,
         @RequestParam("metodo") Short metodo,
         @RequestParam("desrep") String desrep,
         @RequestParam("jrxml") MultipartFile jrxml,
         @RequestParam("jasper") MultipartFile jasper,
         @RequestParam(value = "opciones", required = false) java.util.Set<Short> opciones) {
      try {
         Reportesjr actualizado = repojrService.actualizarCompleto(id, idrepoxopcion, nomrep, metodo, desrep, jrxml,
               jasper, opciones);
         return ResponseEntity.ok(actualizado);
      } catch (IllegalArgumentException e) {
         throw e;
      } catch (Exception e) {
         e.printStackTrace();
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
      }
   }

   // Elimina
   @DeleteMapping("/{idreporte}")
   public ResponseEntity<Void> delete(@PathVariable("idreporte") Short idreporte) {
      repojrService.deleteById(idreporte);
      return ResponseEntity.noContent().build();
   }

   @PostMapping("/{id}/migrar-archivos")
   public ResponseEntity<Reportesjr> migrarArchivos(@PathVariable Short id) throws java.io.IOException {
      return ResponseEntity.ok(repojrService.migrarArchivos(id));
   }

   @PutMapping("/{id}/opciones")
   public ResponseEntity<Reportesjr> asignarOpciones(@PathVariable Short id,
         @org.springframework.web.bind.annotation.RequestBody java.util.Set<Short> opciones) {
      return ResponseEntity.ok(repojrService.asignarOpciones(id, opciones));
   }

   @org.springframework.web.bind.annotation.ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<java.util.Map<String, String>> datosInvalidos(IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(java.util.Collections.singletonMap("message", e.getMessage()));
   }

   @org.springframework.web.bind.annotation.ExceptionHandler(javax.persistence.EntityNotFoundException.class)
   public ResponseEntity<Void> noExiste(javax.persistence.EntityNotFoundException e) {
      return ResponseEntity.notFound().build();
   }

}
