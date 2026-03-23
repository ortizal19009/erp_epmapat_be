package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucio;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EjecucioServicio {

   private final EjecucioR dao;
   // private final PresupueR daoPresupue;

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

   // Cuenta por idparxcer
   public short countByIdparxcer(Long idparxcer) {
      return dao.countByIdparxcer(idparxcer);
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
      Double tmodi = dao.totalModi(codpar + "%", desdeFecha, hastaFecha);
      return tmodi;
   }

   // Devengado de una partida (desde/hasta)
   public Double totalDeven(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tdeven = dao.totalDeven(codpar + "%", desdeFecha, hastaFecha);
      return tdeven;
   }

   // Cobrado de una partida (desde/hasta)
   public Double totalCobpagado(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tdeven = dao.totalCobpagado(codpar + "%", desdeFecha, hastaFecha);
      return tdeven;
   }

   // Ejecución de un Asiento
   public List<Ejecucio> findByIdasiento(Long idasiento) {
      return dao.findByIdasiento(idasiento);
   }

   // Ejecución de un Asiento y tippar
   public List<Ejecucio> findByIdasientoAndTippar(Long idasiento, Integer tippar) {
      return dao.findByIdasientoAndTippar(idasiento, tippar);
   }

   // Ejecucion de una transaci.inttra
   public Optional<Ejecucio> buscarPorInttra(Long inttra) {
      return dao.findByInttra(inttra);
   }

   // Compromisos que tienen saldo pendiente
   public List<Ejecucio> getMisosPendientes(String nomben, Date hasta) {
      return dao.misosPendientes("%" + nomben.toLowerCase() + "%", hasta);
   }

   public Optional<Ejecucio> findById(Long id) {
      return dao.findById(id);
   }

   public <S extends Ejecucio> S save(S entity) {
      return dao.save(entity);
   }

   // nueva.setIntpre(daoPresupue.getReferenceById(nueva.getIntpre().getIntpre()));

   // Guarda: Desde el front se envia dto
   public Ejecucio saveEjecu(Ejecucio nueva) {
      return dao.save(nueva);
   }

   // Actualiza totdeven de la Ejecución
   public void updateTotdeven(Long inteje, BigDecimal totdeven) {
      dao.updateTotdeven(inteje, totdeven);
   }

   // Actualiza (solo los campos modificados)
   @Transactional
   public Ejecucio updateEjecucio(Long inteje, Ejecucio data) {
      Ejecucio ejecucio = findById(inteje)
            .orElseThrow(() -> new ResourceNotFoundExcepciones(
                  "No se encuentra este Id " + inteje));
      // Coloca campos (solo los que se envian )
      if (data.getIntpre() != null)
         ejecucio.setIntpre(data.getIntpre());
      if (data.getCodpar() != null)
         ejecucio.setCodpar(data.getCodpar());
      if (data.getPrmiso() != null)
         ejecucio.setPrmiso(data.getPrmiso());
      if (data.getTotdeven() != null)
         ejecucio.setTotdeven(data.getTotdeven());
      if (data.getDevengado() != null)
         ejecucio.setDevengado(data.getDevengado());
      if (data.getCobpagado() != null)
         ejecucio.setCobpagado(data.getCobpagado());
      if (data.getConcep() != null)
         ejecucio.setConcep(data.getConcep());
      if (data.getIdrefo() != null)
         ejecucio.setIdrefo(data.getIdrefo());
      if (data.getIdtrami() != null)
         ejecucio.setIdtrami(data.getIdtrami());
      if (data.getIdparxcer() != null)
         ejecucio.setIdparxcer(data.getIdparxcer());
      ejecucio.setUsumodi(data.getUsumodi());
      ejecucio.setFecmodi(data.getFecmodi());
      Ejecucio actualizada = dao.save(ejecucio);
      // FALTA: Actualizar saldos y totales
      return actualizada;
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
