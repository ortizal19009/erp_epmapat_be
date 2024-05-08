package com.epmapat.erp_epmapat.servicio;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.repositorio.Fec_facturaR;

@Service
public class Fec_facturaService {

   @Autowired
   private Fec_facturaR dao;

   public List<Fec_factura> findAll() {
      return dao.findAll();
   }

   public <S extends Fec_factura> S save(S entity) {
      return dao.save(entity);
   }

}
