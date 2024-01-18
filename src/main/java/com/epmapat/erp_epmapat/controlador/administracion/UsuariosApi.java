package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.servicio.administracion.UsuarioServicio;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "*")

public class UsuariosApi {

   @Autowired
   UsuarioServicio usuServicio;

   @GetMapping
   public List<Usuarios> getAll() {
      return usuServicio.findAll();
   }

   // Busca
   // @GetMapping("/usuario")
   // public boolean exists(@Param(value = "a") String a, @Param(value = "b")
   // String b) {
   // return usuServicio.existe(a, b);
   // }

   @GetMapping("/usuario")
   public Usuarios getUsuario(@Param(value = "a") String a, @Param(value = "b") String b) {
      if (a != null && b != null) {
         return usuServicio.findUsuario(a, b);
      } else {
         return null;
      }
   }

   // Va a servir para validar
   @GetMapping("/identificacion")
   public Usuarios getByIdentificausu(@Param(value = "identificausu") String identificausu) {
      if (identificausu != null) {
         return usuServicio.findByIdentificausu(identificausu);
      } else {
         return null;
      }
   }

   @GetMapping("/{idusuario}")
   public ResponseEntity<Usuarios> getByIdusuario(@PathVariable Long idusuario) {
      Usuarios usuario = usuServicio.findById(idusuario)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe el Usuario: " + idusuario)));
      return ResponseEntity.ok(usuario);
   }

   @PutMapping("/{idusuario}")
   public ResponseEntity<Usuarios> update(@PathVariable Long idusuario, @RequestBody Usuarios x) {
      Usuarios y = usuServicio.findById(idusuario)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe Usuario con Id: " + idusuario)));
      y.setIdentificausu(x.getIdentificausu());
      y.setNomusu(x.getNomusu());
      y.setCodusu(x.getCodusu());
      y.setFdesde(x.getFdesde());
      y.setFhasta(x.getFhasta());
      y.setEstado(x.getEstado());
      y.setEmail(x.getEmail());
      y.setFeccrea(x.getFeccrea());
      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      y.setOtrapestania(x.getOtrapestania());
      y.setAlias(x.getAlias());

      Usuarios actualizar = usuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }
}
