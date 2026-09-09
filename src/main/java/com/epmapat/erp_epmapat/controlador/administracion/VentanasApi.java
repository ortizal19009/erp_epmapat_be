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

import javax.servlet.http.HttpServletRequest;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.servicio.administracion.VentanaServicio;
import com.epmapat.erp_epmapat.seguridad.WebAccessGuard;

@RestController
@RequestMapping("/ventanas")


public class VentanasApi {

   @Autowired
   VentanaServicio venServicio;
   @Autowired
   WebAccessGuard webAccessGuard;

   @GetMapping
   public Ventanas getAllLista(@Param(value = "idusuario") Long idusuario,
         @Param(value = "nombre") String nombre, HttpServletRequest request) {
      if (idusuario != null && nombre != null) {
         webAccessGuard.requireSelfOrAdmin(request, idusuario);
         return venServicio.findVentana(idusuario, nombre);
      } else {
         return null;
      }
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public ResponseEntity<Ventanas> save(@RequestBody Ventanas x, HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
      return ResponseEntity.ok(venServicio.save(x));
   }

   @PutMapping("/{idventana}")
   public ResponseEntity<Ventanas> update(@PathVariable Long idventana, @RequestBody Ventanas x,
         HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
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
   public ResponseEntity<List<String>> getCatalogoVentanas(HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
      return ResponseEntity.ok(venServicio.findCatalogoVentanas());
   }

   @GetMapping("/catalogo-modulos")
   public ResponseEntity<List<Map<String, Object>>> getCatalogoModulosVentanas(HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
      return ResponseEntity.ok(venServicio.getCatalogoModulosVentanas());
   }

   @PostMapping("/catalogo-modulos")
   public ResponseEntity<Void> saveCatalogoModulosVentanas(@RequestBody List<Map<String, Object>> catalogo,
         HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
      venServicio.saveCatalogoModulosVentanas(catalogo);
      return ResponseEntity.noContent().build();
   }

   @GetMapping("/usuario/{idusuario}")
   public ResponseEntity<List<Map<String, Object>>> getPermisosUsuario(@PathVariable Long idusuario,
         HttpServletRequest request) {
      webAccessGuard.requireSelfOrAdmin(request, idusuario);
      return ResponseEntity.ok(venServicio.getResumenPermisosUsuario(idusuario));
   }

   @PostMapping("/usuario/{idusuario}")
   public ResponseEntity<List<Ventanas>> savePermisosUsuario(
         @PathVariable Long idusuario,
         @RequestBody List<Ventanas> permisos, HttpServletRequest request) {
      webAccessGuard.requireAdmin(request);
      return ResponseEntity.ok(venServicio.savePermisosUsuario(idusuario, permisos));
   }

}
