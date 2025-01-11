package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla5_concep;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla5_concepR;

@Service
public class Tabla5_concepServicio {

   @Autowired
   private Tabla5_concepR dao;

   public List<Tabla5_concep> findAll() {
      return dao.findAll();
   }

   // tipoiva B = Bienes
   public List<Tabla5_concep> obtenerConceptosPorTipoIvaB() {
      return dao.findByTipoIvaB();
   }

   // tipoiva S = Servicios
   public List<Tabla5_concep> obtenerConceptosPorTipoIvaS() {
      return dao.findByTipoIvaS();
   }

   // codporcentaje 3 = 100%
   public List<Tabla5_concep> obtenerConceptosPorCodporcentaje() {
      return dao.findByCodporcentaje();
   }

   public Optional<Tabla5_concep> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Tabla5_concep> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
