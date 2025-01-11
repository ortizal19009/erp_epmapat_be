package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.servicio.contabilidad.PresupueServicio;

//OJO: No debería existir: Hay PreingresosApi y PregastosApi y las llamadas desde el front end se hacen con la ruta preingreso o pregasto, nunca con presupue
@RestController
@RequestMapping("/presupue")
@CrossOrigin(origins = "*")

public class PresupueApi {

   @Autowired
   PresupueServicio presuServicio;

   @GetMapping
   public List<Presupue> getList(
         @Param(value = "tippar") Long tippar,
         @Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      if (codpar != null) {
         return presuServicio.findByCodpar(tippar, codpar);
      } else if (nompar != null) {
         return presuServicio.findByNompar(tippar, nompar.toLowerCase());
      } else if (tippar != null) {
         return presuServicio.findByTippar(tippar);
      }
      return presuServicio.findAll();
   }

   // Partidas por codpar para los datalist
   @GetMapping("/codpar")
   public List<Presupue> findByCodpar(@Param(value = "tippar") Long tippar, @Param(value = "codpar") String codpar) {
      return presuServicio.findByCodpar(tippar, codpar);
   }

   // Partidas de un partida del clasificador
   @GetMapping("/clasificador")
   public List<Presupue> buscaClasificador(@Param(value = "codigo") String codigo) {
      return presuServicio.buscaClasificador(codigo);
   }

   public Presupue updateOrSave(@RequestBody Presupue x) {
      return presuServicio.save(x);
   }

   @GetMapping("/{intpre}")
   public ResponseEntity<Presupue> getByIdNovedad(@PathVariable Long intpre) {
      Presupue x = presuServicio.findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Partida con Intpre: " + intpre)));
      return ResponseEntity.ok(x);
   }

   @PutMapping("/{intpre}")
   public ResponseEntity<Presupue> update(@PathVariable Long intpre, @RequestBody Presupue x) {
      Presupue y = presuServicio.findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Intpre: " + intpre)));
      y.setTippar(x.getTippar());
      y.setCodpar(x.getCodpar());
      y.setCodigo(x.getCodigo());
      y.setNompar(x.getNompar());
      y.setInicia(x.getInicia());
      y.setTotmod(x.getTotmod());
      y.setTotcerti(x.getTotcerti());
      y.setTotmisos(x.getTotmisos());
      y.setTotdeven(x.getTotdeven());
      // y.setObserva(x.getObserva());
      y.setFuncion(x.getFuncion());
      // y.setIdestrfunc(x.getIdestrfunc());
      // y.setIntcla(x.getIntcla());
      y.setCodacti(x.getCodacti());
      y.setCodpart(x.getCodpart());
      y.setSwpluri(x.getSwpluri());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Presupue actualizar = presuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{intpre}")
   private ResponseEntity<Boolean> deletePresupue(@PathVariable("intpre") Long intpre) {
      presuServicio.deleteById(intpre);
      return ResponseEntity.ok(!(presuServicio.findById(intpre) != null));
   }

   @GetMapping("/inicia")
   public Double totalCodpar(@RequestParam Long tippar, String codpar) {
      Double tinicia = presuServicio.totalCodpar(tippar, codpar);
      return tinicia;
   }

}
