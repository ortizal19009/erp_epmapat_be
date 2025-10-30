package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facxrecauda;
import com.epmapat.erp_epmapat.repositorio.FacxrecaudaR;

@Service
public class FacxrecaudaServicio {

   @Autowired
   private FacxrecaudaR dao;

   public <S extends Facxrecauda> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Facxrecauda> findById(Long id) {
      return dao.findById(id);
   }

   public List<Facxrecauda> getByUsuFecha(Long idusuario, Date d, Date h) {
      return dao.getByUsuFecha(idusuario, d, h);
   }

   public Facxrecauda getByIdFactura(Long idfactura) {
      return dao.getyByIdFactura(idfactura);
   }

}