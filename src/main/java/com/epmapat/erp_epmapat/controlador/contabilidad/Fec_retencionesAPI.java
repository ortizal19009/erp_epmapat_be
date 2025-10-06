package com.epmapat.erp_epmapat.controlador.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.servicio.contabilidad.Fec_retencionesServicio;

@RestController
@RequestMapping("/fec_retenciones")
@CrossOrigin(origins = "*")

public class Fec_retencionesAPI {
   
   @Autowired
   private Fec_retencionesServicio fec_reteServicio;

   @GetMapping
   public List<Fec_retenciones> getAll() {
      return fec_reteServicio.findAll();
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Fec_retenciones saveFec_retencion(@RequestBody Fec_retenciones x) {
      return fec_reteServicio.save(x);
   }

}
