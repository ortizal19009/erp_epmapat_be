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

import com.epmapat.erp_epmapat.modelo.contabilidad.Ejecucion;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EjecucionR;

@Service
public class EjecucionServicio {

   @Autowired
   EjecucionR dao;

   public List<Ejecucion> findByCodparFecha(String codpar, Date desdeFecha, Date hastaFecha) {
      return dao.findByCodparFecha(codpar, desdeFecha, hastaFecha);
   }

   public List<Ejecucion> buscaByIdrefo(Long idrefo) {
      return dao.buscaByIdrefo(idrefo);
   }

   // Verifica si una Partida tiene Ejecución
   public boolean verifiEjecucion(String codpar) {
      return dao.tieneEjecucion(codpar);
   }

   public <S extends Ejecucion> S save(S entity) {
      return dao.save(entity);
   }

   public List<Ejecucion> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public Page<Ejecucion> findAll(Pageable pageable) {
      return dao.findAll(pageable);
   }

   public Optional<Ejecucion> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Ejecucion entity) {
      dao.delete(entity);
   }

   // Actualizar codpar
   public List<Ejecucion> actualizarCodpar(Presupue presupue, String nuevoCodpar) {
      List<Ejecucion> ejecuciones = dao.findByidpresupue(presupue);

      if (!ejecuciones.isEmpty()) {
          for (Ejecucion ejecucion : ejecuciones) {
              ejecucion.setCodpar(nuevoCodpar);
          }
          return dao.saveAll(ejecuciones);
      } else {
          throw new NoSuchElementException("No se encontraron registros para el idpresupue proporcionado");
      }
  }

}
