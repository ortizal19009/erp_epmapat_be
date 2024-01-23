package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Formacobro;
import com.epmapat.erp_epmapat.servicio.FormacobroServicio;

@RestController
@RequestMapping("/formacobro")
@CrossOrigin("*")

public class FormacobroApi {

   @Autowired
   FormacobroServicio fcobroServicio;

   @GetMapping
   public List<Formacobro> getAll() {
      return fcobroServicio.findAll();
   }

}
