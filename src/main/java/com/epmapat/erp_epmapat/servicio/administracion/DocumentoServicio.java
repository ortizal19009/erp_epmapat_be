package com.epmapat.erp_epmapat.servicio.administracion;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;
import com.epmapat.erp_epmapat.repositorio.administracion.DocumentosR;

@Service
public class DocumentoServicio {

   @Autowired
   DocumentosR dao;

   public List<Documentos> findByNomdoc(String nomdoc) {
      return dao.findByNomdoc(nomdoc);
   }

   public <S extends Documentos> S save(S entity) {
      return dao.save(entity);
   }

   // public List<Documentos> findAll(Sort sort) {
   //    return dao.findAll(sort);
   // }

   // public Page<Documentos> findAll(Pageable pageable) {
   //    return dao.findAll(pageable);
   // }

   public List<Documentos> findAll() {
      return dao.findAll();
   }

   public Optional<Documentos> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Documentos entity) {
      dao.delete(entity);
   }

}
