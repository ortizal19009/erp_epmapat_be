package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.servicio.Pliego24Servicio;

@RestController
@RequestMapping("/pliego24")
@CrossOrigin("*")

public class Pliego24Api {

   @Autowired
   Pliego24Servicio pli24Servicio;

   //Pliego Tarifario
   @GetMapping
   public List<Pliego24> findTodos() {
      return pli24Servicio.findTodos();
   }

   //Tarifas de todas las Categorias de un determinado consumo (m3) Se usa solo en  la Simulación
   @GetMapping("/consumos")
   public List<Pliego24> findConsumos(@Param(value = "consumo") Long consumo) {
      return pli24Servicio.findConsumos(consumo);
   }

   //Por Categoria y m3 (Bloque)
   @GetMapping("/bloque")
   public List<Pliego24> findBloque( @Param(value = "idcategoria") Long idcategoria, @Param(value = "m3") Long m3 ) {
      return pli24Servicio.findBloque(idcategoria, m3);
   }

}
