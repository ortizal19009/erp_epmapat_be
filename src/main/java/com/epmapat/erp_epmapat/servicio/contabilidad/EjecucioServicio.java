package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.stereotype.Service;


import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucio;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucioR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor

public class EjecucioServicio {

   private final EjecucioR dao;
   private final TramipresuServicio tramiServicio;
   private final PartixcertiServicio parxcerServicio;
   private final PresupueServicio presuServicio;

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

   // Contar por idparxcer
   public short countByIdparxcer(Long idparxcer) {
      return dao.countByIdparxcer(idparxcer);
   }

   // Contar por intpre
   public Long countByIntpre(Long intpre) {
      return dao.countByIntpre(intpre);
   }

   // Reformas de una partida (desde/hasta)
   public Double totalModi(String codpar, Date desdeFecha, Date hastaFecha) {
      Double tmodi = dao.totalModi(codpar + "%", desdeFecha, hastaFecha);
      return tmodi;
   }

   // Partidas de un Trámite
   public List<Ejecucio> partixtrami(Long idtrami) {
      return dao.partixtrami(idtrami);
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

   // Compromisos de una partixcerti
   public List<Ejecucio> obtenerPorIdParxcer(Long idparxcer) {
      return dao.findByIdparxcer(idparxcer);
   }

   // Contar las Partidas de un Trámite
   public short contarPorIdtrami(Long idtrami) {
      return dao.countByIdtrami(idtrami);
   }

   // Ejecucion de una transaci.inttra
   public Optional<Ejecucio> buscarPorInttra(Long inttra) {
      return dao.findByInttra(inttra);
   }

   // Compromisos que tienen saldo pendiente
   public List<Ejecucio> getMisosPendientes(String nomben, Date hasta) {
      return dao.misosPendientes("%" + nomben.toLowerCase() + "%", hasta);
   }

   // Devengados de un compromiso (Busca por: ejecucio.idprmiso)
   public List<Ejecucio> obtenerPorIdPrmiso(Long idprmiso) {
      return dao.findByIdprmiso(idprmiso);
   }

   // Contar lo devengados de un compromiso
   public short contarPorIdprmiso(Long idprmiso) {
      return dao.countByIdprmiso(idprmiso);
   }

   // Ultima Fecha
   public LocalDate obtenerUltimaFechaEje() {
      return dao.findLastFechaEje();
   }

   public Optional<Ejecucio> findById(Long id) {
      return dao.findById(id);
   }

   // Guarda y actualiza Totales
   @Transactional
   public Ejecucio save(Ejecucio nueva) {
      Ejecucio guardada = dao.save(nueva);
      // Actualiza tramipresu.totmiso, partixcerti.totprmisos y presupue.totmisos
      if (guardada.getIdparxcer() != null) { // Es compromiso
         tramiServicio.actualizaTotmiso(guardada.getIdtrami());
         parxcerServicio.actualizaTotprmisos(guardada.getIdparxcer());
         presuServicio.actualizaTotmisos(guardada.getIntpre().getIntpre());
      }
      // Actualiza ejecucio.totdeven
      if (guardada.getIdprmiso() != null) {
         recalculaTotdeven(guardada.getIdprmiso(), guardada.getInteje());
      }
      return guardada;
   }

   // Actualiza totdeven de la Ejecución (Ya se actualiza en nuevo )
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
      // Actualiza totales
      Integer tippar = actualizada.getIntpre().getTippar();
      Long intpre = actualizada.getIntpre().getIntpre();
      Long idtrami = actualizada.getIdtrami();
      Long idparxcer = actualizada.getIdparxcer();
      Long idprmiso = actualizada.getIdprmiso();
      if (tippar != null && tippar == 2 && idparxcer != null) { // Es compromiso
         tramiServicio.actualizaTotmiso(idtrami);
         parxcerServicio.actualizaTotprmisos(idparxcer);
         presuServicio.actualizaTotmisos(intpre);
      }
      if (tippar != null && tippar == 2 && idprmiso != null && idprmiso > 0) {
         recalculaTotdeven(idprmiso, 0L);
      }
      return actualizada;
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

   // Recalcula y actualiza ejecucio.totdeven
   // @Transactional Ya está en la llamada
   private void recalculaTotdeven(Long idprmiso, Long idevenga) {
      BigDecimal totalDevengado = dao.sumaDevengadoPorIdprmiso(idprmiso);
      dao.findById(idprmiso)
            .ifPresent(compromiso -> {
               compromiso.setTotdeven(totalDevengado);
               compromiso.setIdevenga(idevenga);
               dao.save(compromiso);
            });
   }

   // Elimina
   @Transactional
   public Boolean deleteById(Long id) {
      return dao.findById(id)
            .map(ejecucio -> {
               // Guardamos los datos para recalcular
               Integer tippar = ejecucio.getIntpre().getTippar();
               Long intpre = ejecucio.getIntpre().getIntpre();
               Long idtrami = ejecucio.getIdtrami();
               Long idparxcer = ejecucio.getIdparxcer();
               Long idprmiso = ejecucio.getIdprmiso();
               // Elimina
               dao.deleteById(id);
               // Actualiza tramipresu.totmiso, partixcerti.totprmisos y presupue.totmisos
               if (tippar != null && tippar == 2 && idparxcer != null) { // Es compromiso
                  tramiServicio.actualizaTotmiso(idtrami);
                  parxcerServicio.actualizaTotprmisos(idparxcer);
                  presuServicio.actualizaTotmisos(intpre);
               }
               // Si tippar = 2 e idprmiso != null recalcula ejecucio.totdeven
               if (tippar != null && tippar == 2 && idprmiso != null && idprmiso > 0) {
                  recalculaTotdeven(idprmiso, 0L);
               }
               return !dao.existsById(id);
            })
            .orElse(false);
   }

}
