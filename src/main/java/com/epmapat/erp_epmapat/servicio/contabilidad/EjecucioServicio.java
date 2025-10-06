package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucio;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;

@Service
public class EjecucioServicio {

   @Autowired
   EjecucioR dao;

   public List<Ejecucio> findByCodparFecha(String codpar, Date desdeFecha, Date hastaFecha) {
      return dao.findByCodparFecha(codpar, desdeFecha, hastaFecha);
   }

   public List<Ejecucio> buscaByIdrefo(Long idrefo) {
      return dao.buscaByIdrefo(idrefo);
   }

   // Verifica si una Partida tiene Ejecución
   public boolean tieneEjecucio(String codpar) {
      return dao.tieneEjecucio(codpar);
   }

   // Contar por intpre
   public Long countByIntpre(Long intpre) {
      return dao.countByIntpre(intpre);
   }

   // Partidas de un Trámite
   public List<Ejecucio> partixtrami(Long idtrami) {
      return dao.partixtrami(idtrami);
   }

   // Reformas de una partida (desde/hasta)
   public Double totalModi(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tmodi = dao.totalModi(codpar, desdeFecha, hastaFecha);
      return tmodi;
   }

   // Devengado de una partida (desde/hasta)
   public Double totalDeven(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tdeven = dao.totalDeven(codpar, desdeFecha, hastaFecha);
      return tdeven;
   }

   // Cobrado de una partida (desde/hasta)
   public Double totalCobpagado(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tdeven = dao.totalCobpagado(codpar, desdeFecha, hastaFecha);
      return tdeven;
   }

   public <S extends Ejecucio> S save(S entity) {
      return dao.save(entity);
   }

   public List<Ejecucio> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public Page<Ejecucio> findAll(Pageable pageable) {
      return dao.findAll(pageable);
   }

   public Optional<Ejecucio> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Ejecucio entity) {
      dao.delete(entity);
   }

   // Actualizar codpar
   public List<Ejecucio> actualizarCodpar(Presupue presupue, String nuevoCodpar) {
      List<Ejecucio> x = dao.findByintpre(presupue);
      if (!x.isEmpty()) {
         for (Ejecucio y : x) {
            y.setCodpar(nuevoCodpar);
         }
         return dao.saveAll(x);
      } else {
         throw new NoSuchElementException("No se encontraron registros para el intpre proporcionado");
      }
   }

}
