package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Rubroxfac;
import com.epmapat.erp_epmapat.servicio.RubroxfacServicio;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/rubroxfac")
@CrossOrigin(origins = "*")

public class RubroxfacApi {

   @Autowired
   private RubroxfacServicio rxfServicio;

   @GetMapping
   public List<Rubroxfac> getByIdfactura(@Param(value = "idfactura") Long idfactura) {
      return rxfServicio.getByIdfactura(idfactura);
   }

   @GetMapping("/rubro/{idrubro}")
   public List<Rubroxfac> getByIdrubro(@PathVariable Long idrubro) {
      return rxfServicio.getByIdrubro(idrubro);
   }

   @PostMapping
   public Rubroxfac save(@RequestBody Rubroxfac x) {
      return rxfServicio.save(x);
   }

   @GetMapping("/sumavalores")
   public Double findRubroxfac(@RequestParam("idfactura") Long idfactura) {
       return rxfServicio.findRubroxfac(idfactura);
   }

}
