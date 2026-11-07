package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Map;
import java.util.List;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipAsignacion;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.epmapat.erp_epmapat.DTO.contabilidad.SinafipResultado;
import com.epmapat.erp_epmapat.servicio.contabilidad.SinafipServicio;

@RestController
@RequestMapping("/sinafip")
@CrossOrigin("*")
@RequiredArgsConstructor
public class SinafipApi {
   private final SinafipServicio servicio;

   @GetMapping("/vista-previa")
   public SinafipResultado generar(@RequestParam String archivo, @RequestParam int anio, @RequestParam int periodo) {
      return servicio.generar(archivo, anio, periodo);
   }

   @GetMapping("/reciprocas/asignaciones")
   public List<Map<String,Object>> asignaciones(@RequestParam int anio,@RequestParam int periodo) {
      return servicio.asignaciones(anio,periodo);
   }

   @GetMapping("/cuentas")
   public List<Map<String,Object>> cuentas() { return servicio.cuentasSinafip(); }

   @PutMapping("/reciprocas/asignacion")
   public ResponseEntity<Void> asignar(@RequestBody SinafipAsignacion datos) {
      servicio.asignar(datos);
      return ResponseEntity.noContent().build();
   }

   @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<Map<String, String>> invalido(IllegalArgumentException error) {
      return ResponseEntity.badRequest().body(Map.of("message", error.getMessage()));
   }
}
