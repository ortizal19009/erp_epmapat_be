package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.Repoxopcion;
import com.epmapat.erp_epmapat.servicio.administracion.RepoxopcionService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/repoxopcion")
@CrossOrigin("*")

public class RepoxopcionApi {

   private final RepoxopcionService repoxopService;

   // Busca Repoxopcion
   @GetMapping("/busca")
   public ResponseEntity<List<Repoxopcion>> buscaRepoxopcion(@RequestParam String codigo,
         @RequestParam String opcion,
         @RequestParam String nombre) {
      List<Repoxopcion> lista = repoxopService.buscaRepoxopcion(codigo, opcion, nombre);
      return ResponseEntity.ok(lista);
   }

   // Repoxopcion para datalist
   @GetMapping("/datalist")
   public ResponseEntity<List<Repoxopcion>> datalistRepoxopcion(@RequestParam String codigo) {
      List<Repoxopcion> repoxopcion = repoxopService.datalistRepoxopcion(codigo);
      return ResponseEntity.ok(repoxopcion);
   }

   // Repoxopcion por Código y Largo
   @GetMapping("/codigo")
   public ResponseEntity<List<Repoxopcion>> obtenerPorPrefijoYLargo(@RequestParam String codigo) {
      List<Repoxopcion> repoxopcion = repoxopService.obtenerPorPrefijoYLargo(codigo);
      return ResponseEntity.ok(repoxopcion);
   }

   // Valida si existe un codigo (numerico)
   @GetMapping("/valcodigo/{codigo}")
   public ResponseEntity<Boolean> existeCodigo(@PathVariable String codigo) {
      boolean existe = repoxopService.valCodigo(codigo);
      return ResponseEntity.ok(existe);
   }

   // Valida si existe un nombre (ignorando mayúsculas y minúsculas)
   @GetMapping("/valnombre/{nombre}")
   public ResponseEntity<Boolean> existeNombre(@PathVariable String nombre) {
      boolean existe = repoxopService.valNombre(nombre);
      return ResponseEntity.ok(existe);
   }

   // Guardar nuevo
   @PostMapping
   public ResponseEntity<Repoxopcion> save(@RequestBody Repoxopcion x) {
      return ResponseEntity.ok(repoxopService.save(x));
   }

   // Actualiza
   @PutMapping("/{idrepoxopcion}")
   public Repoxopcion actualizar(@PathVariable Short idrepoxopcion, @RequestBody Repoxopcion x) {
      return repoxopService.actualizar(idrepoxopcion, x);
   }

   // Elimina
   @DeleteMapping("/{idrepoxopcion}")
   public ResponseEntity<Void> delete(@PathVariable("idrepoxopcion") Short idrepoxopcion) {
      repoxopService.deleteById(idrepoxopcion);
      return ResponseEntity.noContent().build();
   }

}
