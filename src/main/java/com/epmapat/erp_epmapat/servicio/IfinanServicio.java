package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.epmapat.erp_epmapat.modelo.contabilidad.Ifinan;
import com.epmapat.erp_epmapat.repositorio.IfinanR;

@Service
public class IfinanServicio  {

   @Autowired
   IfinanR dao;

   public List<Ifinan> findByCodifinan(String codifinan) {
      return dao.findByCodifinan( codifinan );
   }

   public List<Ifinan> findByNomifinan(String nomifinan) {
      return dao.findByNomifinan( nomifinan );
   }

   public <S extends Ifinan> S save(S entity) {
      return dao.save(entity);
   }

   public List<Ifinan> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   public Page<Ifinan> findAll(Pageable pageable) {
      return dao.findAll(pageable);
   }

   public Optional<Ifinan> findById(Long id) {
      return dao.findById(id);
   }

   // public Boolean deleteById(Long id) {
   //    if (dao.existsById(id)) {
   //       dao.deleteById(id);
   //       return !dao.existsById(id);
   //    }
   //    return false;
   // }

   public void delete(Ifinan entity) {
      dao.delete(entity);
   }

}
