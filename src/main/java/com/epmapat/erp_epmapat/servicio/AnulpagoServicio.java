package com.epmapat.erp_epmapat.servicio;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.controlador.Anulpago;
import com.epmapat.erp_epmapat.repositorio.AnulpagoR;

@Service
public class AnulpagoServicio {

   @Autowired
   private AnulpagoR dao;

   @SuppressWarnings("null")
   public Optional<Anulpago> findById(Long id) {
      return dao.findById(id);
   }

}