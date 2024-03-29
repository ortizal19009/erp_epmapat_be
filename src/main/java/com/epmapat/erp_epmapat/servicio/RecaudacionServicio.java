package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.interfaces.RecaudadorI;
import com.epmapat.erp_epmapat.modelo.Recaudacion;
import com.epmapat.erp_epmapat.repositorio.RecaudacionR;

@Service
public class RecaudacionServicio {

   @Autowired
   private RecaudacionR dao;

   public <S extends Recaudacion> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Recaudacion> findById(Long id) {
      return dao.findById(id);
   }

   public Double totalRecaudado(Long idrecaudador, Date fechacobro) {
      return dao.totalRecaudado(idrecaudador, fechacobro);
   }

   public List<Recaudacion> findByRecFec(Long idrecaudador, Date d, Date h) {
      return dao.findByRecFec(idrecaudador, d, h);
   }
   public List<Recaudacion> findByFecha(Date d, Date h) {
      return dao.findByFecha( d, h);
   }
   public List<RecaudadorI> findListRecaudador(Date d, Date h) {
      return dao.findListRecaudador( d, h);
   }

}