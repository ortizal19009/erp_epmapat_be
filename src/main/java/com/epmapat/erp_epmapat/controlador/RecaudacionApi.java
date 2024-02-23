package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Recaudacion;
import com.epmapat.erp_epmapat.servicio.RecaudacionServicio;

@RestController
@RequestMapping("/recaudacion")
@CrossOrigin(origins = "*")

public class RecaudacionApi {

   @Autowired
   private RecaudacionServicio recaServicio;

   @GetMapping("/{idrecaudacion}")
   public ResponseEntity<Recaudacion> getByIdrecaudacion(@PathVariable Long idrecaudacion) {
      Recaudacion x = recaServicio.findById(idrecaudacion)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(("No extiste la Recaudacion ID: " + idrecaudacion)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Recaudacion saveRecaudacion(@RequestBody Recaudacion x) {
      return recaServicio.save(x);
   }
}