package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Reformas;
import com.epmapat.erp_epmapat.servicio.contabilidad.ReformaServicio;

@RestController
@RequestMapping("/reformas")
@CrossOrigin(origins = "*")

public class ReformasApi {

   @Autowired
   ReformaServicio refoServicio;

   @GetMapping
   public List<Reformas> getAllLista(
         @Param(value = "desde") Long desde,
         @Param(value = "hasta") Long hasta) {
      return refoServicio.buscaByNumfec(desde, hasta);
   }

   @GetMapping("/ultima")
   public Reformas getUltima() {
      return refoServicio.findFirstByOrderByNumeroDesc();
   }

   @GetMapping("/siguiente")
   public Long getSiguienteReforma() {
      return refoServicio.obtenerSiguienteNumeroReforma();
   }

   @GetMapping("/{idrefo}")
   public ResponseEntity<Reformas> getByIdreforma(@PathVariable Long idrefo) {
      Reformas reforma = refoServicio.findById(idrefo)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Reforma con Id: " + idrefo)));
      return ResponseEntity.ok(reforma);
   }

   @PostMapping
   public ResponseEntity<Reformas> save(@RequestBody Reformas reforma) {
      return ResponseEntity.ok(refoServicio.save(reforma));
   }

   @PutMapping("/{idrefo}")
   public ResponseEntity<Reformas> update(@PathVariable Long idrefo, @RequestBody Reformas x) {
      Reformas y = refoServicio.findById(idrefo)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Reforma con Id: " + idrefo)));
      y.setNumero(x.getNumero());
      y.setFecha(x.getFecha());
      y.setTipo(x.getTipo());
      y.setValor(x.getValor());
      y.setConcepto(x.getConcepto());
      y.setNumdoc(x.getNumdoc());
      y.setIntdoc(x.getIntdoc());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Reformas actualizar = refoServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{idrefo}")
   private ResponseEntity<Boolean> deleteReforma(@PathVariable("idrefo") Long idrefo) {
      refoServicio.deleteById(idrefo);
      return ResponseEntity.ok(!(refoServicio.findById(idrefo) != null));
   }

}
