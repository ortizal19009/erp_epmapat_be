package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Fec_reteimpu;
import com.epmapat.erp_epmapat.repositorio.contabilidad.Fec_reteimpuR;

@Service

public class Fec_reteimpuServicio {

   @Autowired
   private Fec_reteimpuR dao;

   public List<Fec_reteimpu> findAll() {
      return dao.findAll();
   }

   public <S extends Fec_reteimpu> S save(S entity) {
      return dao.save(entity);
   }

}
