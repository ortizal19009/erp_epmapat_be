package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Convenios;
import com.epmapat.erp_epmapat.servicio.ConvenioServicio;

@RestController
@RequestMapping("/convenios")
@CrossOrigin("*")

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
   public ResponseEntity<Convenios> update(@PathVariable Long idconvenio, @RequestBody Convenios x) {
      Convenios y = convServicio.findById(idconvenio)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe el Convenio Id: " + idconvenio)));
      y.setReferencia(x.getReferencia());
      y.setNroautorizacion(x.getNroautorizacion());
      y.setEstado(x.getEstado());
      y.setObservaciones(x.getObservaciones());
      y.setUsuarioeliminacion(x.getUsuarioeliminacion());
      y.setFechaeliminacion(x.getFechaeliminacion());
      y.setRazoneliminacion(x.getRazoneliminacion());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Convenios actualizar = convServicio.save(y);
      return ResponseEntity.ok(actualizar);
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

}
