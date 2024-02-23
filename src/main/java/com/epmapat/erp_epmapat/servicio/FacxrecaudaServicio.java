package com.epmapat.erp_epmapat.servicio;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facxrecauda;
import com.epmapat.erp_epmapat.repositorio.FacxrecaudaR;

@Service
public class FacxrecaudaServicio {

   @Autowired
   private FacxrecaudaR dao;

   @SuppressWarnings("null")
   public <S extends Facxrecauda> S save(S entity) {
      return dao.save(entity);
   }

   @SuppressWarnings("null")
   public Optional<Facxrecauda> findById(Long id) {
      return dao.findById(id);
   }

}