package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Airxrete;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla10;
import com.epmapat.erp_epmapat.repositorio.contabilidad.AirxreteR;

@Service
public class AirxreteServicio {

   @Autowired
   private AirxreteR dao;

   // AIR de una Retención
   public List<Airxrete> getByIdrete(Long idrete) {
      return dao.findByIdrete(idrete);
   }

   public <S extends Airxrete> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Airxrete> findById(Long id) {
      return dao.findById(id);
   }

   // Guarda por lote
   @Transactional
   public List<Airxrete> saveAllBatch(List<Airxrete> entities) {
      return dao.saveAll(entities);
   }

   // Actualiza (Genera las foraneas aqui)
   public Airxrete update(Long idairxrete, Airxrete data) {
      Airxrete existente = dao.findById(idairxrete)
            .orElseThrow(() -> new RuntimeException("AIR no encontrada: " + idairxrete));
      // 1. Reconstruir referencia idrete
      Retenciones ret = new Retenciones();
      ret.setIdrete(data.getIdrete().getIdrete()); // ← SOLO el ID
      existente.setIdrete(ret);
      // 2. Reconstruir referencia idtabla10
      Tabla10 t10 = new Tabla10();
      t10.setIdtabla10(data.getIdtabla10().getIdtabla10());
      existente.setIdtabla10(t10);
      // 3. Actualizar campos simples
      existente.setBaseimpair0(data.getBaseimpair0());
      existente.setBaseimpair12(data.getBaseimpair12());
      existente.setBaseimpairno(data.getBaseimpairno());
      existente.setBaseimpair(data.getBaseimpair());
      existente.setValretair(data.getValretair());
      return dao.save(existente);
   }

   public void deleteById(Long id) {
      dao.deleteById(id);
   }

}

