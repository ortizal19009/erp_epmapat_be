package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla5_concep;
import com.epmapat.erp_epmapat.servicio.contabilidad.Tabla5_concepServicio;

@RestController
@RequestMapping("/tabla5_concep")
@CrossOrigin("*")

public class Tabla5_concepApi {

   @Autowired
   Tabla5_concepServicio tabla5_concepServicio;

   @GetMapping
   public List<Tabla5_concep> findAll() {
      return tabla5_concepServicio.findAll();
   }

   // tipoiva B = Bienes
   @GetMapping("/bienes")
   public ResponseEntity<List<Tabla5_concep>> obtenerConceptosPorTipoIvaB() {
      List<Tabla5_concep> conceptos = tabla5_concepServicio.obtenerConceptosPorTipoIvaB();
      return ResponseEntity.ok(conceptos);
   }

   // tipoiva S = Servicios
   @GetMapping("/servicios")
   public ResponseEntity<List<Tabla5_concep>> obtenerConceptosPorTipoIvaS() {
      List<Tabla5_concep> conceptos = tabla5_concepServicio.obtenerConceptosPorTipoIvaS();
      return ResponseEntity.ok(conceptos);
   }

   // codporcentaje 3 = 100%
   @GetMapping("/cien")
   public ResponseEntity<List<Tabla5_concep>> obtenerConceptosPorCodporcentaje() {
      List<Tabla5_concep> conceptos = tabla5_concepServicio.obtenerConceptosPorCodporcentaje();
      return ResponseEntity.ok(conceptos);
   }

   @PostMapping
   public Tabla5_concep updateOrSave(@RequestBody Tabla5_concep x) {
      return tabla5_concepServicio.save(x);
   }

   @PutMapping("/{idtabla5c}")
   public ResponseEntity<Tabla5_concep> updateTabla5_concep(@PathVariable Long idtabla5c,
         @RequestBody Tabla5_concep x) {
      Tabla5_concep y = tabla5_concepServicio.findById(idtabla5c)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No se encuenta este Id" + idtabla5c));

      y.setConceptoiva(x.getConceptoiva());
      y.setCasillero104(x.getCasillero104());
      y.setCodcue(x.getCodcue());
      y.setIdtabla5(x.getIdtabla5());
      y.setTipo100(x.getTipo100());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Tabla5_concep updateTabla5_concep = tabla5_concepServicio.save(y);
      return ResponseEntity.ok(updateTabla5_concep);
   }

   @GetMapping("/{idtabla5c}")
   public ResponseEntity<Tabla5_concep> getByIdNovedad(@PathVariable Long idtabla5c) {
      Tabla5_concep x = tabla5_concepServicio.findById(idtabla5c)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Cuenta con Id: " + idtabla5c)));
      return ResponseEntity.ok(x);
   }

   @DeleteMapping("/{idtabla5c}")
   private ResponseEntity<Boolean> deleteTabla5_concep(@PathVariable("idtabla5c") Long idtabla5c) {
      tabla5_concepServicio.deleteById(idtabla5c);
      return ResponseEntity.ok(!(tabla5_concepServicio.findById(idtabla5c) != null));
   }

}
