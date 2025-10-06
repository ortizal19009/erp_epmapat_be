package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Liquidafac;
import com.epmapat.erp_epmapat.repositorio.LiquidafacR;

@Service
public class LiquidafacServicio {
   
   @Autowired
   private LiquidafacR dao;

   public List<Liquidafac> findByIdfacturacion(Long idfacturacion) {
      return dao.findByIdfacturacion(idfacturacion);
   }

   public <S extends Liquidafac> S save(S entity) {
      return dao.save(entity);
   }
   
}
