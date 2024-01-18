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
   PresupueServicio preingServicio;

   @GetMapping
   public List<Presupue> getAllLista(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      if (codpar != null) {
         return preingServicio.buscaByCodigoI(codpar);
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
      return preingServicio.findCodigoNombre(codigoNombre);
   }

   @GetMapping("/paringreso")
   public List<Presupue> getParingresos(@Param(value = "codpar") String codpar,
         @Param(value = "nompar") String nompar) {
      return preingServicio.findAllIng(codpar, nompar);
   }

   @GetMapping("/{idpresupue}")
   public ResponseEntity<Presupue> getByIdNovedad(@PathVariable Long idpresupue) {
      Presupue x = preingServicio.findById(idpresupue)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Partida con Idpresupue: " + idpresupue)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Presupue updateOrSave(@RequestBody Presupue x) {
      return preingServicio.save(x);
   }

   @PutMapping("/{idpresupue}")
   public ResponseEntity<Presupue> update(@PathVariable Long idpresupue, @RequestBody Presupue x) {
      Presupue y = preingServicio.findById(idpresupue)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Idpresupue: " + idpresupue)));
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
      y.setIdestrfunc(x.getIdestrfunc());
      y.setCodacti(x.getCodacti());
      y.setIntcla(x.getIntcla());
      y.setCodpart(x.getCodpart());
      y.setSwpluri(x.getSwpluri());
      y.setUsucrea(x.getUsucrea());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());

      Presupue actualizar = preingServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{idpresupue}")
   private ResponseEntity<Boolean> deletePresupue(@PathVariable("idpresupue") Long idpresupue) {
      preingServicio.deleteById(idpresupue);
      return ResponseEntity.ok(!(preingServicio.findById(idpresupue) != null));
   }
}
