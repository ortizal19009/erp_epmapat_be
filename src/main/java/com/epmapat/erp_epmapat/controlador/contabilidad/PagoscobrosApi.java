package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
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

import com.epmapat.erp_epmapat.modelo.contabilidad.Pagoscobros;
import com.epmapat.erp_epmapat.servicio.contabilidad.PagoscobroServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/pagoscobros")
@RequiredArgsConstructor
public class PagoscobrosApi {

   private final PagoscobroServicio pagcobServicio;

   // Pagoscobros de una benextran.idbenxtra con ResponseEntity
   @GetMapping("/idbenxtra")
   public ResponseEntity<List<Pagoscobros>> getByIdBenxtra(@Param("idbenxtra") Long idbenxtra) {
      return ResponseEntity.ok(pagcobServicio.findByIdbenxtra(idbenxtra));
   }

   // Pagoscobros de una idbenxtra sin ResponseEntity
   @GetMapping
   public List<Pagoscobros> getByIdBenxtra1(@Param("idbenxtra") Long idbenxtra) {
      return pagcobServicio.findByIdbenxtra(idbenxtra);
   }

   // Pagoscobros de una benextran.idbenxtra
   @GetMapping("/idbenxtra/{idbenxtra}")
   public ResponseEntity<List<Pagoscobros>> findByIdbenxtra(@PathVariable Long idbenxtra) {
      List<Pagoscobros> lista = pagcobServicio.findByIdbenxtra(idbenxtra);
      return lista.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(lista);
   }

   // Pagoscobros de una transaci.inttra
   @GetMapping("/inttra/{inttra}")
   public ResponseEntity<List<Pagoscobros>> getByInttra(@PathVariable Long inttra) {
      List<Pagoscobros> lista = pagcobServicio.findByInttra(inttra);
      return lista.isEmpty()
            ? ResponseEntity.noContent().build()
            : ResponseEntity.ok(lista);
   }

   // Guardar nuevo
   @PostMapping
   public Pagoscobros savePagocobro(@RequestBody Pagoscobros pagoscobros) {
      return pagcobServicio.savePagocobro(pagoscobros);
   }

   // Actualiza
   @PutMapping("/{idpagcob}")
   public ResponseEntity<Pagoscobros> updatePagoscobro(
         @PathVariable Long idpagcob,
         @RequestBody Pagoscobros data) {
      Pagoscobros actualizado = pagcobServicio.updatePagoscobros(idpagcob, data);
      return ResponseEntity.ok(actualizado);
   }

   // Elimina (Devuelve: 200, 204 o 500 )
   @DeleteMapping("/{idpagcob}")
   public ResponseEntity<?> deletePagoscobros(@PathVariable Long idpagcob) {
      try {
         boolean eliminado = pagcobServicio.deleteById(idpagcob);
         if (eliminado) {
            return ResponseEntity.ok(true); // 200 OK
         } else {
            return ResponseEntity.noContent().build(); // 204 No Content
         }
      } catch (Exception ex) {
         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // 500
      }
   }

}
