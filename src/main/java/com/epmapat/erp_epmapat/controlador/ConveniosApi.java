package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.ConvenioDetalle;
import com.epmapat.erp_epmapat.interfaces.ConvenioOneData;
import com.epmapat.erp_epmapat.interfaces.EstadoConvenios;
import com.epmapat.erp_epmapat.modelo.Convenios;
import com.epmapat.erp_epmapat.servicio.ConvenioServicio;

@RestController
@RequestMapping("/convenios")

public class ConveniosApi {

   @Autowired
   private ConvenioServicio convServicio;

   @GetMapping("/DesdeHasta")
   public List<Convenios> conveniosDesdeHasta(@Param(value = "desde") Integer desde,
         @Param(value = "hasta") Integer hasta) {
      return convServicio.conveniosDesdeHasta(desde, hasta);
   }

   // Ultimo Nroconvenio
   @GetMapping("/ultimo")
   public Convenios ultimoNroconvenio() {
      return convServicio.ultimoNroconvenio();
   }

   // Siguiente Nroconvenio
   @GetMapping("/siguiente")
   public Integer siguienteNroconvenio() {
      return convServicio.siguienteNroconvenio();
   }

   // Valida Nroconvenio
   @GetMapping("/valNroconvenio")
   // public ResponseEntity<Boolean> valNroconvenio(@RequestParam Integer
   // nroconvenio) {
   public ResponseEntity<Boolean> valNroconvenio(@Param(value = "nroconvenio") Integer nroconvenio) {
      boolean b = convServicio.valNroconvenio(nroconvenio);
      return ResponseEntity.ok(b);
   }

   @GetMapping("/{idconvenio}")
   public ResponseEntity<Convenios> getById(@PathVariable Long idconvenio) {
      Convenios x = convServicio.findById(idconvenio)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe el Convenio Id: " + idconvenio)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Convenios saveConvenios(@RequestBody Convenios x) {
      return convServicio.save(x);
   }

   @PutMapping("/{idconvenio}")
   public ResponseEntity<Convenios> update(@PathVariable Long idconvenio,
         @RequestBody Convenios x,
         @RequestParam(required = false, defaultValue = "0") Long usumodi,
         @RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
         @RequestParam(required = false, defaultValue = "Actualización de convenio") String observacion) {

      Convenios actualizado = convServicio.actualizarConvenioConAuditoria(idconvenio, x, usumodi, observacion, tipo);
      return ResponseEntity.ok(actualizado);
   }

   @PutMapping("/{idconvenio}/estado")
   public ResponseEntity<Convenios> updateEstado(@PathVariable Long idconvenio,
         @RequestParam Integer estado,
         @RequestParam(required = false, defaultValue = "0") Long usumodi,
         @RequestParam(required = false, defaultValue = "Cambio de estado de convenio") String observacion,
         @RequestParam(required = false, defaultValue = "CAMBIO_DE_ESTADO") String tipo) {

      Convenios actualizado = convServicio.actualizarEstadoConvenioConAuditoria(idconvenio, estado, usumodi, observacion, tipo);
      return ResponseEntity.ok(actualizado);
   }

   @DeleteMapping(value = "/{idconvenio}")
   private ResponseEntity<Boolean> delete(@PathVariable("idconvenio") Long idconvenio) {
      convServicio.deleteById(idconvenio);
      return ResponseEntity.ok(!(convServicio.findById(idconvenio) != null));
   }

   @GetMapping("/referencia")
   private ResponseEntity<List<Convenios>> getByReferencia(@RequestParam("referencia") Long referencia) {
      List<Convenios> convenios = convServicio.findByReferencia(referencia);
      return ResponseEntity.ok(convenios);
   }

   @GetMapping("/estados")
   public ResponseEntity<List<EstadoConvenios>> getEstadoByConvenios() {
      return ResponseEntity.ok(convServicio.getEstadoByConvenios());
   }

   @GetMapping("/pendientesPago")
   public ResponseEntity<Page<EstadoConvenios>> getByFacPendientes(@RequestParam Long d, @RequestParam Long h,
         @RequestParam int page, @RequestParam int size) {
      return ResponseEntity.ok(convServicio.getByFacPendientes(d, h, page, size));
   }

   @GetMapping("/buscar")
   public ResponseEntity<Page<ConvenioDetalle>> buscarConvenios(
         @RequestParam(required = false) Integer nroDesde,
         @RequestParam(required = false) Integer nroHasta,
         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaDesde,
         @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate fechaHasta,
         @RequestParam(required = false) String nombre,
         @RequestParam(required = false) Integer estado,
         @RequestParam(required = false) Long minPendientes,
         @RequestParam(required = false) Long maxPendientes,
         @RequestParam(required = false) Long idabonado,
         @RequestParam(required = false) Long cuenta,
         @RequestParam(defaultValue = "0") int page,
         @RequestParam(defaultValue = "20") int size) {

      Long cuentaFiltro = idabonado != null ? idabonado : cuenta;
      return ResponseEntity.ok(
            convServicio.buscarConvenios(nroDesde, nroHasta, fechaDesde, fechaHasta, nombre, estado, minPendientes, maxPendientes, cuentaFiltro, page, size));
   }

   @GetMapping("/sin-pendientes")
   public ResponseEntity<List<ConvenioDetalle>> getConveniosSinPendientes(
         @RequestParam(required = false) Integer estado) {
      return ResponseEntity.ok(convServicio.getConveniosSinPendientes(estado));
   }

   @GetMapping("/con-pendientes")
   public ResponseEntity<List<ConvenioDetalle>> getConveniosConPendientes(
         @RequestParam(required = false) Integer estado) {
      return ResponseEntity.ok(convServicio.getConveniosConPendientes(estado));
   }

   @PutMapping("/marcar-pagados")
   public ResponseEntity<List<Convenios>> marcarConveniosPagados(
         @RequestParam(required = false, defaultValue = "0") Long usumodi,
         @RequestParam(required = false, defaultValue = "Actualización masiva de convenios pagados") String observacion,
         @RequestParam(required = false, defaultValue = "CAMBIO_DE_ESTADO") String tipo) {
      return ResponseEntity.ok(convServicio.marcarConveniosPagados(usumodi, observacion, tipo));
   }

   @GetMapping("/pendiente")
   public ResponseEntity<List<EstadoConvenios>> gePendienteByConvenio(@RequestParam Long idconvenio) {
      return ResponseEntity.ok(convServicio.gePendienteByConvenio(idconvenio));
   }

   @GetMapping("/datosOne")
   public ResponseEntity<List<ConvenioOneData>> getDatosConvenio(@RequestParam Long idconvenio) {
      return ResponseEntity.ok(convServicio.findDatosConvenio(idconvenio));
   }

}
