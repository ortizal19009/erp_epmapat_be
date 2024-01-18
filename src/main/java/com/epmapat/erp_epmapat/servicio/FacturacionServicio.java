package com.epmapat.erp_epmapat.servicio;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.Facturacion;
import com.epmapat.erp_epmapat.repositorio.FacturacionR;

@Service
public class FacturacionServicio {

   @Autowired
   private FacturacionR dao;

   public List<Facturacion> findDesdeHasta(Long desde, Long hasta, Date del, Date al) {
      if (desde != null || hasta != null) {
         return dao.findDesdeHasta(desde, hasta, del, al);
      } else {
         // return dao.findAll();
         return null;
      }
   }

   // Busca por Cliente (simpres dentro de fechas)
   public List<Facturacion> findByCliente(String cliente, Date del, Date al) {
      return dao.findByCliente(cliente, del, al);
   }

   // Busca la última Facturación
   public Facturacion ultimo() {
      return dao.findFirstByOrderByIdfacturacionDesc();
   }

   public Optional<Facturacion> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Facturacion> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}
