package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.modelo.contabilidad.Reformas;
import com.epmapat.erp_epmapat.repositorio.contabilidad.ReformasR;

@Service
public class ReformaServicio {

   @Autowired
   ReformasR dao;

   public List<Reformas> buscaByNumfec(Long desde, Long hasta) {
      return dao.buscaByNumfec(desde, hasta);
   }

   public Long obtenerSiguienteNumeroReforma() {
      Reformas ultimaReforma = dao.findTopByOrderByNumeroDesc();
      if (ultimaReforma != null) {
         Long ultimoNumeroReforma = ultimaReforma.getNumero();
         return ultimoNumeroReforma + 1;
      } else {
         return 1L;
      }
   }

   // Busca la última Reforma
   public Reformas findFirstByOrderByNumeroDesc() {
      return dao.findFirstByOrderByNumeroDesc();
   }

   public <S extends Reformas> S save(S entity) {
      return dao.save(entity);
   }

   public List<Reformas> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public Page<Reformas> findAll(Pageable pageable) {
      return dao.findAll(pageable);
   }

   public Optional<Reformas> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Reformas entity) {
      dao.delete(entity);
   }

}
