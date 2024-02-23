package com.epmapat.erp_epmapat.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Recaudacion;
import com.epmapat.erp_epmapat.repositorio.RecaudacionR;

@Service
public class RecaudacionServicio {

   @Autowired
   private RecaudacionR dao;

   @SuppressWarnings("null")
   public <S extends Recaudacion> S save(S entity) {
      return dao.save(entity);
   }

   @SuppressWarnings("null")
   public Optional<Recaudacion> findById(Long id) {
      return dao.findById(id);
   }

}