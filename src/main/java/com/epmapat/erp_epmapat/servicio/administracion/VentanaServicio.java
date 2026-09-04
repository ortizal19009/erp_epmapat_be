package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.text.Normalizer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;
import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulosxventanas;
import com.epmapat.erp_epmapat.repositorio.administracion.ErpmodulosR;
import com.epmapat.erp_epmapat.repositorio.administracion.ErpmodulosxventanasR;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;
import com.epmapat.erp_epmapat.repositorio.administracion.UsrxmodulosR;

@Service
public class VentanaServicio {
   private static final long PERMISO_ADMIN = 3L;
   private static final String COLOR1_DEFAULT = "rgb(80, 4, 80)";
   private static final String COLOR2_DEFAULT = "rgb(250, 200, 250)";
   private static final Set<String> VENTANAS_ADMINISTRACION_CENTRAL = Set.of();
   private static final List<String> VENTANAS_BASE = List.of(
         "documentos",
         "usuarios",
         "perfil-usuario",
         "reportesjr",
         "configuracion-impresion",
         "admin-access-control",
         "admin-correos",
         "admin-mobile-apk",
         "tabla4",
         "definir",
         "condonaciones",
         "condonaciones-pendientes",
         "clientes",
         "abonados",
         "emisiones",
         "suspensiones",
         "habilitaciones",
         "rutas",
         "pliego",
         "pliego24",
         "facturacion",
         "facturas",
         "fecfactura",
         "catalogoitems",
         "generadorxml",
         "usoitems",
         "rubros",
         "aguatramite",
         "ctramites",
         "reclamos",
         "ccertificaciones",
         "recaudaciones",
         "transferencias",
         "anulaciones-bajas",
         "convenios",
         "ntacredito",
         "cajas",
         "categorias",
         "ptoemision",
         "estadom",
         "intereses",
         "nacionalidades",
         "novedades",
         "tpidentificas",
         "tipopago",
         "tpreclamos",
         "ubicacionm");

   @Autowired
   VentanasR dao;
   @Autowired
   UsrxmodulosR modulosDao;
   @Autowired
   ErpmodulosxventanasR modulosVentanasDao;
   @Autowired
   ErpmodulosR erpmodulosDao;

   public Ventanas findVentana(Long idusuario, String nombre) {
      return findVentanaNormalizada(idusuario, nombre);
   }

   public <S extends Ventanas> S save(S x) {
      return dao.save(x);
   }

   public Optional<Ventanas> findById(Long id) {
      return dao.findById(id);
   }

   public List<Ventanas> findByUsuario(Long idusuario) {
      return dao.findByIdusuarioOrderByNombreAsc(idusuario);
   }

   public List<String> findCatalogoVentanas() {
      Map<String, String> catalogo = new LinkedHashMap<>();
      VENTANAS_BASE.forEach(nombre -> catalogo.put(nombre.toLowerCase(), nombre));
      dao.findDistinctNombres().forEach(nombre -> {
         if (nombre != null && !nombre.isBlank()) {
            catalogo.putIfAbsent(nombre.trim().toLowerCase(), nombre.trim());
         }
      });
      return new ArrayList<>(catalogo.values());
   }

   public List<String> findCatalogoVentanasUsuario(Long idusuario) {
      if (idusuario != null && idusuario == 1L) {
         return findCatalogoVentanas();
      }

      List<Long> modulosActivos = modulosDao.findActiveModuleIdsByUser(idusuario);
      if (modulosActivos == null || modulosActivos.isEmpty()) {
         return Collections.emptyList();
      }

      return modulosVentanasDao.findByModuleIds(modulosActivos).stream()
            .map(Erpmodulosxventanas::getNombreventana)
            .filter(nombre -> nombre != null && !nombre.isBlank())
            .distinct()
            .toList();
   }

   public List<Map<String, Object>> getResumenPermisosUsuario(Long idusuario) {
      Map<String, Ventanas> permisosActuales = new LinkedHashMap<>();
      dao.findByIdusuarioOrderByNombreAsc(idusuario).forEach(v -> {
         if (v.getNombre() != null && !v.getNombre().isBlank()) {
            permisosActuales.put(v.getNombre().trim().toLowerCase(), v);
         }
      });

      Map<String, String> modulosPorVentana = getModulosPorVentana(idusuario);
      List<Map<String, Object>> rows = new ArrayList<>();
      for (String nombre : findCatalogoVentanasUsuario(idusuario)) {
         String key = nombre == null ? "" : nombre.trim().toLowerCase();
         if (key.isBlank()) {
            continue;
         }
         Ventanas actual = permisosActuales.get(key);
         Map<String, Object> row = new LinkedHashMap<>();
         row.put("idventana", actual != null ? actual.getIdventana() : null);
         row.put("nombre", nombre);
         row.put("modulo", modulosPorVentana.getOrDefault(key, "Sin modulo asignado"));
         row.put("idusuario", idusuario);
         row.put("permissions", actual != null && actual.getPermissions() != null ? actual.getPermissions() : 0L);
         row.put("color1", actual != null ? actual.getColor1() : COLOR1_DEFAULT);
         row.put("color2", actual != null ? actual.getColor2() : COLOR2_DEFAULT);
         rows.add(row);
      }
      return rows;
   }

   private String getModuloVentana(String nombreVentana) {
      String key = nombreVentana == null ? "" : nombreVentana.trim().toLowerCase();
      return VENTANAS_ADMINISTRACION_CENTRAL.contains(key)
            ? "Administración central"
            : "Comercialización";
   }

   public List<Map<String, Object>> getCatalogoModulosVentanas() {
      Map<String, Erpmodulosxventanas> asignaciones = new LinkedHashMap<>();
      modulosVentanasDao.findAllWithModule().forEach(asignacion ->
            asignaciones.put(asignacion.getNombreventana().trim().toLowerCase(), asignacion));

      List<Map<String, Object>> rows = new ArrayList<>();
      for (String nombre : findCatalogoVentanas()) {
         Erpmodulosxventanas asignacion = asignaciones.get(nombre.trim().toLowerCase());
         Map<String, Object> row = new LinkedHashMap<>();
         row.put("nombre", nombre);
         row.put("iderpmodulo", asignacion == null ? null : asignacion.getIderpmodulo().getIderpmodulo());
         row.put("modulo", asignacion == null ? null : asignacion.getIderpmodulo().getDescripcion());
         rows.add(row);
      }
      return rows;
   }

   @Transactional
   public void saveCatalogoModulosVentanas(List<Map<String, Object>> catalogo) {
      if (catalogo == null) {
         throw new IllegalArgumentException("Debe enviar el catalogo de ventanas.");
      }

      Map<String, String> ventanasValidas = new LinkedHashMap<>();
      findCatalogoVentanas().forEach(nombre -> ventanasValidas.put(nombre.trim().toLowerCase(), nombre));
      modulosVentanasDao.deleteAllInBatch();

      for (Map<String, Object> item : catalogo) {
         String nombre = item == null || item.get("nombre") == null ? "" : item.get("nombre").toString().trim();
         Long idModulo = getLong(item == null ? null : item.get("iderpmodulo"));
         String nombreCanonico = ventanasValidas.get(nombre.toLowerCase());
         if (nombreCanonico == null || idModulo == null) {
            continue;
         }

         Erpmodulos modulo = erpmodulosDao.findById(idModulo)
               .orElseThrow(() -> new IllegalArgumentException("El modulo indicado no existe."));
         if (!esModuloWeb(modulo)) {
            throw new IllegalArgumentException("Las ventanas WEB solo pueden asignarse a módulos WEB o BOTH.");
         }
         Erpmodulosxventanas asignacion = new Erpmodulosxventanas();
         asignacion.setNombreventana(nombreCanonico);
         asignacion.setIderpmodulo(modulo);
         modulosVentanasDao.save(asignacion);
      }
   }

   private Map<String, String> getModulosPorVentana(Long idusuario) {
      List<Erpmodulosxventanas> asignaciones;
      if (idusuario != null && idusuario == 1L) {
         asignaciones = modulosVentanasDao.findAllWithModule();
      } else {
         List<Long> modulos = modulosDao.findActiveModuleIdsByUser(idusuario);
         asignaciones = modulos == null || modulos.isEmpty()
               ? Collections.emptyList()
               : modulosVentanasDao.findByModuleIds(modulos);
      }

      Map<String, String> resultado = new LinkedHashMap<>();
      asignaciones.forEach(asignacion -> resultado.put(
            asignacion.getNombreventana().trim().toLowerCase(),
            asignacion.getIderpmodulo().getDescripcion()));
      return resultado;
   }

   private Long getLong(Object value) {
      if (value instanceof Number) {
         return ((Number) value).longValue();
      }
      if (value == null || value.toString().isBlank()) {
         return null;
      }
      try {
         return Long.valueOf(value.toString());
      } catch (NumberFormatException e) {
         return null;
      }
   }

   private boolean esModuloWeb(Erpmodulos modulo) {
      if (modulo == null || modulo.getPlatform() == null) {
         return true;
      }
      String plataforma = modulo.getPlatform().trim().toUpperCase();
      return "WEB".equals(plataforma) || "BOTH".equals(plataforma);
   }

   @Transactional
   public List<Ventanas> savePermisosUsuario(Long idusuario, List<Ventanas> permisos) {
      if (idusuario == null) {
         throw new IllegalArgumentException("Debe indicar el usuario.");
      }
      if (permisos == null || permisos.isEmpty()) {
         return Collections.emptyList();
      }

      List<Ventanas> guardados = new ArrayList<>();
      Set<String> permitidas = new HashSet<>();
      findCatalogoVentanasUsuario(idusuario).forEach(nombre -> permitidas.add(nombre.trim().toLowerCase()));
      for (Ventanas item : permisos) {
         if (item == null || item.getNombre() == null || item.getNombre().isBlank()) {
            continue;
         }

         String nombre = item.getNombre().trim();
         if (!permitidas.contains(nombre.toLowerCase())) {
            continue;
         }
         Ventanas actual = findVentanaNormalizada(idusuario, nombre);
         if (actual == null) {
            actual = new Ventanas();
            actual.setIdusuario(idusuario);
            actual.setNombre(nombre);
         }

         actual.setPermissions(item.getPermissions() == null ? 0L : item.getPermissions());
         actual.setColor1(tieneTexto(item.getColor1()) ? item.getColor1().trim()
               : (tieneTexto(actual.getColor1()) ? actual.getColor1() : COLOR1_DEFAULT));
         actual.setColor2(tieneTexto(item.getColor2()) ? item.getColor2().trim()
               : (tieneTexto(actual.getColor2()) ? actual.getColor2() : COLOR2_DEFAULT));
         guardados.add(dao.save(actual));
      }
      return guardados;
   }

   public boolean hasPermission(Long idusuario, String nombre, long minimo) {
      if (idusuario == null || nombre == null || nombre.isBlank()) {
         return false;
      }
      if (idusuario == 1L) {
         return true;
      }

      boolean ventanaActiva = findCatalogoVentanasUsuario(idusuario).stream()
            .anyMatch(catalogo -> catalogo.equalsIgnoreCase(nombre.trim()));
      if (!ventanaActiva) {
         return false;
      }

      Ventanas ventana = findVentanaNormalizada(idusuario, nombre);
      if (ventana == null || ventana.getPermissions() == null) {
         return false;
      }
      return ventana.getPermissions() >= minimo;
   }

   public boolean canApproveCondonaciones(Long idusuario) {
      return hasPermission(idusuario, "condonaciones-pendientes", PERMISO_ADMIN);
   }

   private Ventanas findVentanaNormalizada(Long idusuario, String nombre) {
      if (idusuario == null || nombre == null || nombre.isBlank()) {
         return null;
      }

      List<Ventanas> resultados = dao.findByIdusuarioAndNombreNormalizado(idusuario, nombre.trim());
      return resultados.isEmpty() ? null : resultados.get(0);
   }

   private String normalizar(String valor) {
      if (valor == null) {
         return "";
      }
      return Normalizer.normalize(valor, Normalizer.Form.NFD)
            .replaceAll("\\p{M}", "")
            .trim()
            .toUpperCase();
   }

   private boolean tieneTexto(String valor) {
      return valor != null && !valor.isBlank();
   }
}
