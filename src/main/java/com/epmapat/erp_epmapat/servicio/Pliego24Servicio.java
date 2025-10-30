package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Pliego24;
import com.epmapat.erp_epmapat.repositorio.Pliego24R;

@Service
public class Pliego24Servicio {

   @Autowired
   Pliego24R dao;

   //Pliego Tarifario
   public List<Pliego24> findTodos() {
      return dao.findTodos();
   }

   // Tarifas de todas las categorias de un determinado consumo (m3) Se usa solo en la simulación
   public List<Pliego24> findConsumos(Long m3) {
      return dao.findConsumos(m3);
   }

   // Tarifa de un determinado consumo (m3), de una categoria y de una Gradualidad
   public List<Pliego24> findBloque(Long idcategoria, Long m3) {
      return dao.findBloque(idcategoria, m3);
   }

}
