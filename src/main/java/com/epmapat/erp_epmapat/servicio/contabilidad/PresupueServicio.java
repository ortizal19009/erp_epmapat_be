package com.epmapat.erp_epmapat.servicio.contabilidad;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.repositorio.contabilidad.PresupueR;

@Service
public class PresupueServicio {

   @Autowired
   private PresupueR dao;

   public List<Presupue> findAllIng(String codpar, String nompar) {
      return dao.findAllIng(codpar, nompar);
   }

   // Busca por Código o Nombre
   public List<Presupue> findCodigoNombre(String codigoNombre) {
      return dao.findCodigoNombre(codigoNombre);
   }

   public List<Presupue> buscaByCodigoI(String codpar) {
      return dao.buscaByCodigoI(codpar);
   }

   // Busca por Tipo, Código y Nombre
   public List<Presupue> findByTipoCodigoyNombre(Integer tippar, String codpar, String nompar) {
      return dao.findByTipoCodigoyNombre(tippar, codpar, nompar);
   }

   //Validar Código
   public List<Presupue> buscaByCodigo(String codpar) {
      return dao.buscaByCodpar(codpar);
   }

   // public List<Presupue> buscaByNombreI(String nombre) {
   //    return dao.buscaByNombreG(nombre);
   // }

   // public List<Presupue> buscaByNombreG(String nombre) {
   //    return dao.buscaByNombreG(nombre);
   // }

   public <S extends Presupue> S save(S entity) {
      return dao.save(entity);
   }

   public List<Presupue> findAll() {
      return dao.findAll();
   }

   // Cuenta por Actividad
   public Long countByIdestrfunc(Long idestrfunc) {
      return dao.countByIdestrfunc(idestrfunc);
   }

   // Partidas por Actividad
   public List<Presupue> findByActividad(Long idestrfunc) {
      return dao.findByActividad(idestrfunc);
   }

   public Optional<Presupue> findById(Long id) {
      return dao.findById(id);
   }

   public Boolean deleteById(Long id) {
      if (dao.existsById(id)) {
         dao.deleteById(id);
         return !dao.existsById(id);
      }
      return false;
   }

   public void delete(Presupue entity) {
      dao.delete(entity);
   }
}
