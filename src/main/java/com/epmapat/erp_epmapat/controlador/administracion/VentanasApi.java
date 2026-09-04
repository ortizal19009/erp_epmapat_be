package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.servicio.administracion.VentanaServicio;

@RestController
@RequestMapping("/ventanas")


public class VentanasApi {

   @Autowired
   VentanaServicio venServicio;

   @GetMapping
   public Ventanas getAllLista(@Param(value = "idusuario") Long idusuario,
         @Param(value = "nombre") String nombre) {
      if (idusuario != null && nombre != null) {
         return venServicio.findVentana(idusuario, nombre);
      } else {
         return null;
      }
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public ResponseEntity<Ventanas> save(@RequestBody Ventanas x) {
      return ResponseEntity.ok(venServicio.save(x));
   }

   @PutMapping("/{idventana}")
   public ResponseEntity<Ventanas> update(@PathVariable Long idventana, @RequestBody Ventanas x) {
      Ventanas y = venServicio.findById(idventana)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Ventana con Id: " + idventana)));
      y.setNombre(x.getNombre());
      y.setColor1(x.getColor1());
      y.setColor2(x.getColor2());
      y.setIdusuario(x.getIdusuario());
      y.setPermissions(x.getPermissions());

      Ventanas actualizar = venServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @GetMapping("/catalogo")
   public ResponseEntity<List<String>> getCatalogoVentanas() {
      return ResponseEntity.ok(venServicio.findCatalogoVentanas());
   }

   @GetMapping("/catalogo-modulos")
   public ResponseEntity<List<Map<String, Object>>> getCatalogoModulosVentanas() {
      return ResponseEntity.ok(venServicio.getCatalogoModulosVentanas());
   }

   @PostMapping("/catalogo-modulos")
   public ResponseEntity<Void> saveCatalogoModulosVentanas(@RequestBody List<Map<String, Object>> catalogo) {
      venServicio.saveCatalogoModulosVentanas(catalogo);
      return ResponseEntity.noContent().build();
   }

   @GetMapping("/usuario/{idusuario}")
   public ResponseEntity<List<Map<String, Object>>> getPermisosUsuario(@PathVariable Long idusuario) {
      return ResponseEntity.ok(venServicio.getResumenPermisosUsuario(idusuario));
   }

   @PostMapping("/usuario/{idusuario}")
   public ResponseEntity<List<Ventanas>> savePermisosUsuario(
         @PathVariable Long idusuario,
         @RequestBody List<Ventanas> permisos) {
      return ResponseEntity.ok(venServicio.savePermisosUsuario(idusuario, permisos));
   }

}
