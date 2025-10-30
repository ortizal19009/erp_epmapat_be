package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;

@Service
public class VentanaServicio {

   @Autowired
   VentanasR dao;

   public Ventanas findVentana(Long idusuario, String nombre) {
      return dao.findByIdusuarioAndNombre(idusuario, nombre);
   }

   public <S extends Ventanas> S save(S x) {
      return dao.save( x );
   }

   public Optional<Ventanas> findById(Long id) {
      return dao.findById(id);
   }

}
