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
import com.epmapat.erp_epmapat.modelo.contabilidad.Estrfunc;
import com.epmapat.erp_epmapat.servicio.contabilidad.EstrfuncServicio;

import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;

@RestController
@RequestMapping("/estrfunc")
@CrossOrigin(origins = "*")

public class EstrfuncApi {

   @Autowired
   EstrfuncServicio estrfuncServicio;

   @GetMapping
   public List<Estrfunc> findAll(@Param(value = "codigo") String codigo, @Param(value = "nombre") String nombre) {
      if (codigo != null) {
         return estrfuncServicio.findByCodigo(codigo);
      } else {
         if (nombre != null) {
            return estrfuncServicio.findByNombre(nombre.toLowerCase());
         } else
            return estrfuncServicio.findAll(Sort.by(Sort.Order.asc("codigo")));
      }
   }

   // Busca por Código o Nombre (un solo campo)
   @GetMapping("/codigoNombre")
   public List<Estrfunc> getCodigoNombre(@Param(value = "codigoNombre") String codigoNombre) {
      return estrfuncServicio.findCodigoNombre(codigoNombre.toLowerCase());
   }

   @GetMapping("/{intest}")
   public ResponseEntity<Estrfunc> getByIdNovedad(@PathVariable Long intest) {
      Estrfunc x = estrfuncServicio.findById(intest)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Cuenta con Id: " + intest)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Estrfunc updateOrSave(@RequestBody Estrfunc x) {
      return estrfuncServicio.save(x);
   }

   @PutMapping("/{intest}")
   public ResponseEntity<Estrfunc> update(@PathVariable Long intest, @RequestBody Estrfunc x) {
      Estrfunc y = estrfuncServicio.findById(intest)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Actividad con Id: " + intest)));
      y.setCodigo(x.getCodigo());
      y.setNombre(x.getNombre());
      y.setMovimiento(x.getMovimiento());
      y.setObjcosto(x.getObjcosto());

      Estrfunc actualizar = estrfuncServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @DeleteMapping("/{intest}")
   private ResponseEntity<Boolean> deleteEstrfunc(@PathVariable("intest") Long intest) {
      estrfuncServicio.deleteById(intest);
      return ResponseEntity.ok(!(estrfuncServicio.findById(intest) != null));
   }

}
