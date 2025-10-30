package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.Acceso;
import com.epmapat.erp_epmapat.servicio.administracion.AccesoServicio;

@RestController
@RequestMapping("/acceso")
@CrossOrigin(origins = "*")

public class AccesoApi {

   @Autowired
   AccesoServicio accServicio;

   @GetMapping
   public List<Acceso> getAll() {
      return accServicio.findAll();
   }

}
