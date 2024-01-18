package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.administracion.Colores;
import com.epmapat.erp_epmapat.servicio.administracion.ColorServicio;

@RestController
@RequestMapping("/colores")
@CrossOrigin(origins = "*")

public class ColoresApi {

   @Autowired
   ColorServicio coloServicio;

   @GetMapping("/tonos")
   public List<Colores> findTonos() {
      return coloServicio.findTonos();
   }

   @GetMapping
   public List<Colores> findByTono(@Param(value = "codigo") String codigo) {
      if (codigo != null) {
         return coloServicio.findByTono(codigo);
      } else {
         return null;
      }
   }

}
