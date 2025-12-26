package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.HashMap;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.LoginRequest;
import com.epmapat.erp_epmapat.DTO.LoginResponse;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.UsuarioI;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.servicio.administracion.UsuarioServicio;

@RestController
@RequestMapping("/usuarios")

public class UsuariosApi {

   @Autowired
   UsuarioServicio usuServicio;

   @GetMapping
   public List<Usuarios> getAll() {
      return usuServicio.findAll();
   }

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
      y.setPriusu(x.getPriusu());
      y.setPerfil(x.getPerfil());
      y.setToolbarframe(x.getToolbarframe());
      y.setToolbarsheet(x.getToolbarsheet());

      Usuarios actualizar = usuServicio.save(y);
      return ResponseEntity.ok(actualizar);
   }

   @PostMapping
   public ResponseEntity<Object> saveUsuario(@RequestBody Usuarios user) {
      Map<String, Object> response = new HashMap<>();
      Usuarios _user = usuServicio.save(user);
      if (_user != null) {
         response.put("status", ResponseEntity.ok());
         response.put("message", "Usuario creado");

      } else {
         response.put("status", ResponseEntity.ok());
         response.put("message", "Usuario no creado");
      }
      return ResponseEntity.ok(response);

   }

   @GetMapping("one")
   public ResponseEntity<UsuarioI> findDatosById(@RequestParam Long idusuario) {
      UsuarioI usuario = usuServicio.findDatosById(idusuario);
      return ResponseEntity.ok(usuario);
   }

   @PostMapping("/login")
   public ResponseEntity<?> login(@RequestBody LoginRequest request) {

      UsuarioI user = usuServicio.chargeLogin(request.getUsername());
      String pass = myFun(request.getPassword());

      if (user == null) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
      }

      boolean ok = user.getNomusu().equals(request.getUsername())
            && user.getCodusu().equals(pass);

      if (!ok) {
         return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciales incorrectas");
      }

      String platform = request.getPlatform(); // "MOBILE" o "WEB"
      String access = user.getPlataform_access(); // "MOBILE" / "WEB" / "BOTH"

      // ✅ Regla A: permiso de login por plataforma
      boolean allowedPlatform = "BOTH".equals(access) || platform.equals(access);

      if (!allowedPlatform) {
         return ResponseEntity.status(HttpStatus.FORBIDDEN)
               .body("Usuario no autorizado para " + platform);
      }

      // ✅ Regla B: módulos habilitados para esa plataforma
      List<String> modules = usuServicio.getEnabledModules(user.getIdusuario(), platform);

      return ResponseEntity.ok(
            new LoginResponse("token-jwt-falso", request.getUsername(), request.getUserId(), modules));
   }

   public static String myFun(String x) {
      StringBuilder y = new StringBuilder();

      // 1. Concatenar los códigos ASCII de cada carácter
      for (int i = 0; i < x.length(); i++) {
         y.append((int) x.charAt(i));
      }

      StringBuilder rtn = new StringBuilder();

      // 2. Tomar caracteres en posiciones pares de y
      for (int i = 0; i < y.length(); i += 2) {
         rtn.append(y.charAt(i));
      }

      // 3. Agregar longitud del string sin espacios a los lados
      rtn.append(x.trim().length());

      // 4. Tomar caracteres en posiciones impares desde el final
      for (int i = y.length() - 1; i >= 0; i -= 2) {
         rtn.append(y.charAt(i));
      }

      return rtn.toString();
   }

   @GetMapping("/ping")
   public ResponseEntity<String> ping() {
      return ResponseEntity.ok("OK");
   }

}
