package com.epmapat.erp_epmapat.controlador.administracion;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.Eliminadosapp;
import com.epmapat.erp_epmapat.servicio.administracion.EliminadoappService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/eliminadosapp")
@CrossOrigin("*")

public class EliminadosappApi {

   private final EliminadoappService elimService;

   @PostMapping
   public ResponseEntity<Eliminadosapp> save(@RequestBody Eliminadosapp x) {
      return ResponseEntity.ok(elimService.save(x));
   }

}
