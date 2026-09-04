package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
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
import com.epmapat.erp_epmapat.config.AESUtil;
import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.interfaces.UsuarioI;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.modelo.rrhh.Personal;
import com.epmapat.erp_epmapat.servicio.administracion.UsuarioServicio;
import com.epmapat.erp_epmapat.servicio.rrhh.PersonalServicio;

@RestController
@RequestMapping("/usuarios")

public class UsuariosApi {
   private static final int PERFIL_MAX_LENGTH = 30;

   @Autowired
   UsuarioServicio usuServicio;
   @Autowired
   PersonalServicio personalServicio;

   @GetMapping
   public List<Usuarios> getAll() {
      return usuServicio.findAll();
   }

   @GetMapping("/with-personal")
   public ResponseEntity<List<Map<String, Object>>> getAllWithPersonal() {
      List<Map<String, Object>> rows = usuServicio.findAll().stream().map(usuario -> {
         Map<String, Object> row = new LinkedHashMap<>();
         row.put("idusuario", usuario.getIdusuario());
         row.put("identificausu", usuario.getIdentificausu());
         row.put("nomusu", usuario.getNomusu());
         row.put("alias", usuario.getAlias());
         row.put("email", usuario.getEmail());
         row.put("estado", usuario.getEstado());
         row.put("perfil", usuario.getPerfil());
         row.put("plataform_access", usuario.getPlataform_access());

         Personal personal = usuario.getPersonal();
         if (personal != null) {
            row.put("personalIdpersonal", personal.getIdpersonal());
            row.put(
                  "personalNombre",
                  String.join(" ",
                        personal.getApellidos() == null ? "" : personal.getApellidos().trim(),
                        personal.getNombres() == null ? "" : personal.getNombres().trim()).trim());

            Map<String, Object> personalMap = new LinkedHashMap<>();
            personalMap.put("idpersonal", personal.getIdpersonal());
            personalMap.put("apellidos", personal.getApellidos());
            personalMap.put("nombres", personal.getNombres());
            personalMap.put("identificacion", personal.getIdentificacion());
            row.put("personal", personalMap);
         } else {
            row.put("personalIdpersonal", null);
            row.put("personalNombre", null);
            row.put("personal", null);
         }

         return row;
      }).collect(Collectors.toList());

      return ResponseEntity.ok(rows);
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
   public ResponseEntity<?> update(@PathVariable Long idusuario, @RequestBody Usuarios x) {

      Usuarios y = usuServicio.findById(idusuario)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe Usuario con Id: " + idusuario));

      y.setIdentificausu(x.getIdentificausu());
      y.setNomusu(x.getNomusu());
      if (StringUtils.hasText(x.getCodusu())) {
         y.setCodusu(x.getCodusu());
      }
      y.setFdesde(x.getFdesde());
      y.setFhasta(x.getFhasta());
      if (x.getEstado() != null) {
         y.setEstado(x.getEstado());
      }
      y.setEmail(x.getEmail());

      // ⚠️ no tocar feccrea en update
      // y.setFeccrea(x.getFeccrea());

      y.setUsumodi(x.getUsumodi());
      y.setFecmodi(x.getFecmodi());
      if (x.getOtrapestania() != null) {
         y.setOtrapestania(x.getOtrapestania());
      }
      y.setAlias(x.getAlias());
      y.setPriusu(x.getPriusu());
      y.setPerfil(normalizePerfil(x.getPerfil()));
      if (x.getToolbarframe() != null) {
         y.setToolbarframe(x.getToolbarframe());
      }
      if (x.getToolbarsheet() != null) {
         y.setToolbarsheet(x.getToolbarsheet());
      }
      if (StringUtils.hasText(x.getPlataform_access())) {
         y.setPlataform_access(x.getPlataform_access());
      }

      // ✅ ACTUALIZAR RELACIÓN PERSONAL
      if (x.getPersonal() == null) {
         // si mandan null, desvinculas
         y.setPersonal(null);
      } else if (x.getPersonal().getIdpersonal() != null) {
         Long idpersonal = x.getPersonal().getIdpersonal();

         Personal per;
         try {
            per = personalServicio.findById(idpersonal);
         } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(Map.of("message", "No existe Personal con id: " + idpersonal));
         }

         y.setPersonal(per);
      }

      try {
         Usuarios actualizar = usuServicio.save(y);
         return ResponseEntity.ok(actualizar);
      } catch (DataIntegrityViolationException ex) {
         return ResponseEntity.status(HttpStatus.CONFLICT)
               .body(Map.of("message", "No se pudo actualizar el usuario por una restricción de datos"));
      } catch (IllegalArgumentException ex) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(Map.of("message", ex.getMessage()));
      }
   }

   @PutMapping("/{idusuario}/perfil")
   public ResponseEntity<?> updatePerfil(@PathVariable Long idusuario, @RequestBody Map<String, Object> payload) {
      Usuarios usuario = usuServicio.findById(idusuario)
            .orElseThrow(() -> new ResourceNotFoundExcepciones("No existe Usuario con Id: " + idusuario));

      try {
         Object perfil = payload.get("perfil");
         usuario.setPerfil(normalizePerfil(perfil == null ? null : String.valueOf(perfil)));
         usuario.setUsumodi(
               payload.get("usumodi") == null ? usuario.getUsumodi() : Long.valueOf(String.valueOf(payload.get("usumodi"))));
         Usuarios actualizado = usuServicio.save(usuario);
         return ResponseEntity.ok(actualizado);
      } catch (DataIntegrityViolationException ex) {
         return ResponseEntity.status(HttpStatus.CONFLICT)
               .body(Map.of("message", "No se pudo actualizar el perfil por una restricción de datos"));
      } catch (IllegalArgumentException ex) {
         return ResponseEntity.status(HttpStatus.BAD_REQUEST)
               .body(Map.of("message", ex.getMessage()));
      }
   }

   private String normalizePerfil(String perfil) {
      if (perfil == null) {
         return null;
      }

      String perfilNormalizado = perfil.trim();
      if (perfilNormalizado.length() > PERFIL_MAX_LENGTH) {
         throw new IllegalArgumentException(
               "El perfil no puede exceder " + PERFIL_MAX_LENGTH + " caracteres.");
      }
      return perfilNormalizado;
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

      // 1️⃣ Buscar usuario
      String username = request.getUsername() == null ? "" : request.getUsername().trim();
      String password = request.getPassword() == null ? "" : request.getPassword().trim();
      UsuarioI user = usuServicio.chargeLogin(username);
      if (user == null) {
         return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body("Credenciales incorrectas");
      }

      // 2️⃣ Validar contraseña
      String passEncrypt = myFun(password);
      boolean credencialesOk = user.getCodusu() != null
            && user.getCodusu().equals(passEncrypt);

      if (!credencialesOk) {
         return ResponseEntity
               .status(HttpStatus.UNAUTHORIZED)
               .body("Credenciales incorrectas");
      }

      // 3️⃣ Validar plataforma (MOBILE / WEB / BOTH)
      String platform = request.getPlatform();
      String access = user.getPlataform_access();

      // Normalizar valores
      platform = (platform == null || platform.isBlank())
            ? "MOBILE"
            : platform.trim().toUpperCase();

      access = (access == null || access.isBlank())
            ? "BOTH"
            : access.trim().toUpperCase();

      boolean allowedPlatform = "BOTH".equals(access) || platform.equals(access);

      if (!allowedPlatform) {
         return ResponseEntity
               .status(HttpStatus.FORBIDDEN)
               .body("Usuario no autorizado para plataforma " + platform);
      }

      // 4️⃣ Obtener módulos habilitados por usuario + plataforma
      List<String> modules = usuServicio.getEnabledModules(user.getIdusuario(), platform);

      // 5️⃣ Respuesta exitosa
      LoginResponse response = new LoginResponse(
            "token-jwt-falso", // luego JWT real
            user.getNomusu(), // username
            user.getIdusuario(), // userId REAL
            user.getCargo(), // profile
            modules // módulos habilitados
      );

      return ResponseEntity.ok(response);
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

   @GetMapping("/cargo")
   public ResponseEntity<List<UsuarioI>> getByCargo(
         @RequestParam(name = "ids") List<Long> idsCargo) {

      List<UsuarioI> usuarios = usuServicio.findByCargo(idsCargo);
      return ResponseEntity.ok(usuarios);
   }

   @GetMapping("/descifrar")
   public ResponseEntity<?> getPassword(@RequestParam String pass) throws Exception {
      return ResponseEntity.ok(AESUtil.descifrar(pass));
   }

}
