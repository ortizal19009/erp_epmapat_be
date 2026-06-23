package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;
import com.epmapat.erp_epmapat.servicio.contabilidad.TramipresuServicio;

@RestController
@RequestMapping("/tramipresu")

public class TramipresuApi {

   @Autowired
   private TramipresuServicio tramiServicio;

   @GetMapping
   public List<Tramipresu> getAllLista(@Param(value = "desdeNum") Long desdeNum,
         @Param(value = "hastaNum") Long hastaNum,
         @Param("desdeFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate desdeFecha,
         @Param("hastaFecha") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate hastaFecha) {
      if (desdeNum != null) {
         return tramiServicio.findDesdeHasta(desdeNum, hastaNum, desdeFecha, hastaFecha);
      } else
         // return certiServicio.findAll();
         return null;
   }

   @GetMapping("/max")
   public ResponseEntity<Tramipresu> getMax() {
      return ResponseEntity.ok(tramiServicio.findFirstByOrderByNumeroDesc());
   }

   // Valida número
   @GetMapping("/valnumero/{numero}")
   public boolean valNumero(@PathVariable Long numero) {
      return tramiServicio.valNumero(numero);
   }

   // Un Trámite por Número (Retorna 200:Ok 204: noContent)
   @GetMapping("/buscanumero/{numero}")
   public ResponseEntity<Tramipresu> buscaPorNumero(@PathVariable Long numero) {
      Tramipresu trami = tramiServicio.buscaPorNumero(numero);
      return (trami != null)
            ? ResponseEntity.ok(trami) // 200 OK con cuerpo
            : ResponseEntity.noContent().build(); // 204 No Content sin cuerpo
   }

   @GetMapping("/idtrami")
   public ResponseEntity<Optional<Tramipresu>> findById(@RequestParam("idtrami") Long idtrami) {
      return ResponseEntity.ok(tramiServicio.findById(idtrami));
   }

   @PostMapping
   public ResponseEntity<Tramipresu> saveTramipresu(@RequestBody Tramipresu nueva) {
      return ResponseEntity.ok(tramiServicio.save(nueva));
   }

   // Actualiza solo los modificados Ok con Patch
   @PatchMapping("/{idtrami}")
   public ResponseEntity<Tramipresu> updateTramipresu(@PathVariable Long idtrami, @RequestBody Tramipresu tramipresu) {
      Tramipresu actualizada = tramiServicio.updateTramipresu(idtrami, tramipresu);
      return ResponseEntity.ok(actualizada);
   }

   // Elimina: devuelve 200 con true o false y en error 500
   @DeleteMapping("/{idtrami}")
   public ResponseEntity<Map<String, Object>> deleteTramipresu(@PathVariable Long idtrami) {
      Map<String, Object> response = new HashMap<>();
      try {
         boolean deleted = tramiServicio.deleteById(idtrami);
         if (deleted) {
            response.put("deleted", true);
            response.put("message", "El registro fue eliminado correctamente.");
         } else {
            response.put("deleted", false);
            response.put("message", "El registro no existía o ya había sido eliminado.");
         }
         return ResponseEntity.ok(response); // 200 en ambos casos
      } catch (Exception ex) {
         response.put("error", true);
         response.put("message", "Se produjo un error al intentar eliminar el registro.");
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
      }
   }

}
