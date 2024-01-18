package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Liquidafac;
import com.epmapat.erp_epmapat.servicio.LiquidafacServicio;

@RestController
@RequestMapping("/liquidafac")
@CrossOrigin("*")

public class LiquidafacApi {

   @Autowired
   private LiquidafacServicio liqfacServicio;

   @GetMapping
   public List<Liquidafac> getByIdfacturacion(@Param(value = "idfacturacion") Long idfacturacion) {
      if (idfacturacion != null)
         return liqfacServicio.findByIdfacturacion(idfacturacion);
      else
         return null;
   }

   @PostMapping
   public ResponseEntity<Liquidafac> save(@RequestBody Liquidafac liquidafac) {
       return ResponseEntity.ok(liqfacServicio.save( liquidafac ));
   }

}
