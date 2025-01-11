package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;

@Service
public class RetencionesServicio {

   @Autowired
   private RetencionesR dao;

   // Busca por secuencial y fechas
   public List<Retenciones> findDesdeHasta(String desdeSecu, String hastaSecu, Date desdeFecha, Date hastaFecha) {
      return dao.findDesdeHasta(desdeSecu, hastaSecu, desdeFecha, hastaFecha);
   }

   public Retenciones findFirstByOrderBySecretencion1Desc() {
      return dao.findFirstByOrderBySecretencion1Desc();
   }

   // Valida Secretencion1
   public boolean valSecretencion1(String secretencion1) {
      return dao.valSecretencion1(secretencion1);
   }

   public List<Retenciones> findAll() {
      return dao.findAll();
   }

   public Optional<Retenciones> findById(Long id) {
      return dao.findById(id);
   }

   //Retencion(es) de un asiento
   public List<Retenciones> findByIdasiento(Long idasiento) {
      return dao.findByIdasiento(idasiento);
   }

   public <S extends Retenciones> S save(S entity) {
      return dao.save(entity);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}