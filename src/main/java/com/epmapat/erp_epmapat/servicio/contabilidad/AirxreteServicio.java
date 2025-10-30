package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Airxrete;
import com.epmapat.erp_epmapat.repositorio.contabilidad.AirxreteR;

@Service
public class AirxreteServicio {

   @Autowired
   private AirxreteR dao;

   // public List<Airxrete> findAll() {
   // return dao.findAll();
   // }

   // AIR de una Retención
   public List<Airxrete> getByIdrete(Long idrete) {
      return dao.findByIdrete(idrete);
   }

   public <S extends Airxrete> S save(S entity) {
      return dao.save(entity);
   }

}
