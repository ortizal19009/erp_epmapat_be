package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Airxrete;
import com.epmapat.erp_epmapat.servicio.contabilidad.AirxreteServicio;

@RestController
@RequestMapping("/airxrete")

public class AirxreteApi {

   @Autowired
   private AirxreteServicio airxreteServicio;

   @GetMapping("/retencion")
   public ResponseEntity<List<Airxrete>> getByIdrete(@RequestParam Long idrete) {
      return ResponseEntity.ok(airxreteServicio.getByIdrete(idrete));
   }

   @PostMapping
   public Airxrete saveAirxrete(@RequestBody Airxrete airxrete) {
      return airxreteServicio.save(airxrete);
   }

   // Guarda en batch
   @PostMapping("/batch")
   public ResponseEntity<List<Airxrete>> saveBatch(@RequestBody List<Airxrete> entities) {
      List<Airxrete> saved = airxreteServicio.saveAllBatch(entities);
      return ResponseEntity.ok(saved);
   }

   // Actualiza un AIR
   @PutMapping("/{idairxrete}")
   public ResponseEntity<Airxrete> updateAirxrete(
         @PathVariable Long idairxrete,
         @RequestBody Airxrete airxrete) {
      Airxrete updated = airxreteServicio.update(idairxrete, airxrete);
      return ResponseEntity.ok(updated);
   }

   // Elimina airxrete
   // @DeleteMapping("/{idairxrete}")
   // public ResponseEntity<Boolean> deleteAirxrete(@PathVariable("idrete") Long
   // idrete) {
   // airxreteServicio.deleteById(idrete);
   // return ResponseEntity.ok(!(airxreteServicio.findById(idrete) != null));
   // }

   @DeleteMapping("/{idairxrete}")
   public ResponseEntity<Void> deleteAirxrete(@PathVariable Long idairxrete) {
      // Opción A: más segura y explícita (recomendada en la mayoría de proyectos)
      if (airxreteServicio.findById(idairxrete).isEmpty()) {
         return ResponseEntity.notFound().build();
      }
      airxreteServicio.deleteById(idairxrete);
      return ResponseEntity.noContent().build();

      // ────────────────────────────────────────
      // Opción B: más corta (muy común si no te importa distinguir 404 vs 204)
      // airxreteServicio.deleteById(id);
      // return ResponseEntity.noContent().build();
   }
}
