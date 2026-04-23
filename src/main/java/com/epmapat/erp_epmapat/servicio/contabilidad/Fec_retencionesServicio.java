package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_retencionesR;

@Service
public class Fec_retencionesServicio {
   
   @Autowired
   private Fec_retencionesR dao;

   public List<Fec_retenciones> findAll() {
      return dao.findAll();
   }

   public Optional<Fec_retenciones> findById(Long id) {
      return dao.findById(id);
   }

   public Fec_retenciones findFirstByClaveacceso(String claveacceso) {
      return dao.findFirstByClaveaccesoOrderByIdretencionDesc(claveacceso);
   }

   public boolean existsByClaveaccesoAndEstadoIn(String claveacceso, List<String> estados) {
      return dao.existsByClaveaccesoAndEstadoIn(claveacceso, estados);
   }

   public <S extends Fec_retenciones> S save(S entity) {
      return dao.save(entity);
   }

}
