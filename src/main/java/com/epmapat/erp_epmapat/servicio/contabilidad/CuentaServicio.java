package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Sort;

import com.epmapat.erp_epmapat.modelo.contabilidad.Cuentas;
import com.epmapat.erp_epmapat.repositorio.contabilidad.CuentasR;

@Service
public class CuentaServicio {

   @Autowired
   private CuentasR dao;

   public List<Cuentas> findAll() {
      return dao.findAll();
   }

   // Busca lista de Cuentas por Código y/o Nombre
   public List<Cuentas> findByCodigoyNombre(String codcue, String nomcue) {
      return dao.findByCodigoyNombre(codcue, nomcue);
   }

   public List<Cuentas> findByNomcue(String nomcue) {
      return dao.findByNomcue(nomcue);
   }

   public List<Cuentas> findByCodcue(String codcue) {
      return dao.findByCodcue(codcue);
   }

   public List<Cuentas> findByGrucue(String grucue) {
      return dao.findByGrucue(grucue);
   }

   public List<Cuentas> findByAsohaber(String asohaber) {
      return dao.findByAsohaber(asohaber);
   }

   public List<Cuentas> findByAsodebe(String asodebe) {
      return dao.findByAsodebe(asodebe);
   }

   // Cuentas por Tiptran para el DataList
   public List<Cuentas> findByTiptran(Integer tiptran, String codcue ) {
      return dao.findByTiptran(tiptran, codcue );
   }

   public <S extends Cuentas> S save(S entity) {
      return dao.save(entity);
   }

   public List<Cuentas> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   // public Page<Cuentas> findAll(Pageable pageable) {
   // return dao.findAll(pageable);
   // }

   public Optional<Cuentas> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Cuentas entity) {
      dao.delete(entity);
   }

}
