package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.data.domain.Sort;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.Pliego24Dto;
import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.servicio.Pliego24Servicio;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pliego24")
public class Pliego24Api {

   @Autowired
   Pliego24Servicio pli24Servicio;

   //Pliego Tarifario
   @GetMapping
   public List<Pliego24Dto> findTodos() {
      return pli24Servicio.findTodos().stream()
          .map(this::toDto)
          .collect(Collectors.toList());
   }

   //Tarifas de todas las Categorias de un determinado consumo (m3) Se usa solo en la Simulación
   @GetMapping("/consumos")
   public List<Pliego24Dto> findConsumos(@Param(value = "consumo") Long consumo) {
      return pli24Servicio.findConsumos(consumo).stream()
          .map(this::toDto)
          .collect(Collectors.toList());
   }

   //Por Categoria y m3 (Bloque)
   @GetMapping("/bloque")
   public List<Pliego24Dto> findBloque( @Param(value = "idcategoria") Long idcategoria, @Param(value = "m3") Long m3 ) {
      return pli24Servicio.findBloque(idcategoria, m3).stream()
          .map(this::toDto)
          .collect(Collectors.toList());
   }

   private Pliego24Dto toDto(Pliego24 p) {
       return Pliego24Dto.builder()
           .idpliego(p.getIdpliego())
           .desde(p.getDesde())
           .hasta(p.getHasta())
           .agua(p.getAgua())
           .saneamiento(p.getSaneamiento())
           .porc(p.getPorc())
           .idcategoria(p.getIdcategoria() != null ? p.getIdcategoria().getIdcategoria() : null)
           .build();
   }

}
