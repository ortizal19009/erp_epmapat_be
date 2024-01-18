package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.Facxconvenio;
import com.epmapat.erp_epmapat.servicio.FacxconvenioServicio;

@RestController
@RequestMapping("/facxconvenio")
@CrossOrigin("*")

public class FacxconvenioApi {

   @Autowired
   FacxconvenioServicio fxconvServicio;

   @GetMapping()
   public List<Facxconvenio> findByConvenio(@Param(value = "idconvenio") Long idconvenio) {
      if (idconvenio != null) {
         return fxconvServicio.findByConvenio(idconvenio);
      } else {
         return fxconvServicio.find10();
      }
   }

   @GetMapping("/{idfacxconvenio}")
   public ResponseEntity<Facxconvenio> getById(@PathVariable Long idfacxconvenio) {
      Facxconvenio x = fxconvServicio.findById(idfacxconvenio)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  ("No existe la Factura  Id: " + idfacxconvenio)));
      return ResponseEntity.ok(x);
   }

   @PostMapping
   public Facxconvenio saveFacxconvenio(@RequestBody Facxconvenio x) {
      return (fxconvServicio.save( x ));
   }

}
