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

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_reteimpu;
import com.epmapat.erp_epmapat.servicio.contabilidad.Fec_reteimpuServicio;

@RestController
@RequestMapping("/fec_reteimpu")
@CrossOrigin(origins = "*")

public class Fec_reteimpuApi {

   @Autowired
   private Fec_reteimpuServicio fec_reteimpuServicio;

   @GetMapping
   public List<Fec_reteimpu> getAll() {
      return fec_reteimpuServicio.findAll();
   }

   @PostMapping
   @ResponseStatus(HttpStatus.CREATED)
   public Fec_reteimpu saveFec_reteimpu(@RequestBody Fec_reteimpu x) {
      return fec_reteimpuServicio.save(x);
   }

}
