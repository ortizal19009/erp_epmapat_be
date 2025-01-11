package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Estrfunc;
import com.epmapat.erp_epmapat.repositorio.contabilidad.EstrfuncR;

@Service
public class EstrfuncServicio {

   @Autowired
   private EstrfuncR dao;

   public List<Estrfunc> findAll(Sort sort) {
      return dao.findAll(sort);
   }

   // Validar por Código
   public List<Estrfunc> findByCodigo(String codigo) {
      return dao.findByCodigo(codigo);
   }

   // Validar por Nombre
   public List<Estrfunc> findByNombre(String nombre) {
      return dao.findByNombre(nombre);
   }

   // Busca por Código o Nombre
   public List<Estrfunc> findCodigoNombre(String codigoNombre) {
      return dao.findCodigoNombre(codigoNombre);
   }

   public <S extends Estrfunc> S save(S entity) {
      return dao.save(entity);
   }

   public Optional<Estrfunc> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Estrfunc entity) {
      dao.delete(entity);
   }

}
