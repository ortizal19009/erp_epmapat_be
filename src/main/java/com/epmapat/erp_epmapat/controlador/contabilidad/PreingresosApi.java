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
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.servicio.contabilidad.PresupueServicio;

@RestController
@RequestMapping("/preingresos")
@CrossOrigin("*")

public class PreingresosApi {

   @Autowired
   PresupueServicio presuServicio;

   @GetMapping
   public List<Presupue> getAllLista(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      if (codpar != null) {
         return presuServicio.buscaByCodigoI(codpar);
      }
      if (nompar != null) {
         // return preingServicio.buscaByNombreI(nompar.toLowerCase());
         return null;
      }
      return null;
   }

   // Busca por Código o Nombre
   @GetMapping("/codigoNombre")
   public List<Presupue> getCodigoNombre(@Param(value = "codigoNombre") String codigoNombre) {
      return presuServicio.findCodigoNombre(codigoNombre);
   }

   @GetMapping("/paringreso")
   public List<Presupue> getParingresos(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      return presuServicio.findAllIng(codpar, nompar);
   }

   // Busca Partidas de Ingresos (Para cálculos con todas las partidas)
   @GetMapping("/partidas")
   public List<Presupue> buscaPartidas(@Param(value = "tippar") Integer tippar) {
      return presuServicio.buscaPartidas(tippar);
   }

   @GetMapping("/{intpre}")
   public ResponseEntity<Presupue> getByIdNovedad(@PathVariable Long intpre) {
      Presupue x = presuServicio.findById(intpre)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Partida con Intpre: " + intpre)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Presupue updateOrSave(@RequestBody Presupue x) {
      return presuServicio.save(x);
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

}
