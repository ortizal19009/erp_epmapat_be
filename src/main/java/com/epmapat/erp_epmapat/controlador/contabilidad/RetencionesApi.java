package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;

@RestController
@RequestMapping("/retenciones")


public class RetencionesApi {

  @Autowired
   private RetencionesServicio reteServicio;

   @GetMapping
   public List<Retenciones> getAllLista(@Param(value = "idasiento") Long idasiento) {
      if (idasiento != null) {
         return reteServicio.findByIdasiento(idasiento);
      } else
         return reteServicio.findAll();
   }

   @GetMapping("/desdehasta")
   public List<Retenciones> getDesdeHasta(
         @Param(value = "desdeSecu") String desdeSecu,
         @Param(value = "hastaSecu") String hastaSecu,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") Date hastaFecha) {
            System.out.println("POR AQUI");
      return reteServicio.findDesdeHasta(desdeSecu, hastaSecu, desdeFecha, hastaFecha);
   }

   @GetMapping("/{idrete}")
   public Optional<Retenciones> findByIdRetenciones(@PathVariable Long idrete) {
      return reteServicio.findById(idrete);
   }

   @GetMapping("/ultimo")
   public Retenciones ultimo() {
      return reteServicio.findLastNumeric();
   }

   // Valida Secretencion1
   @GetMapping("/valSecretencion1/{secretencion1}")
   public ResponseEntity<Boolean> valSecretencion1(
         @PathVariable Integer secretencion1) {
      boolean esValido = reteServicio.valSecretencion1(secretencion1);
      return ResponseEntity.ok(esValido);
   }

   // Guarda Nuevo
   @PostMapping
   public Retenciones saveRetencion(@RequestBody Retenciones retencion) {
      return reteServicio.save(retencion);
   }

   // Actualiza
   @PutMapping("/{idrete}")
   public ResponseEntity<Retenciones> updateRetencion(
         @PathVariable Long idrete,
         @RequestBody Retenciones retencion) {
      Retenciones updated = reteServicio.updateRetencion(idrete, retencion);
      return ResponseEntity.ok(updated);
   }

   // Elimina (Si no existe devuelve 404)
   @DeleteMapping("/{idrete}")
   public ResponseEntity<?> deleteRetenciones(@PathVariable Long idrete) {
      try {
         reteServicio.deleteById(idrete);
         return ResponseEntity.ok(true);
      } catch (EntityNotFoundException ex) {
         return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
      }
   }

}
