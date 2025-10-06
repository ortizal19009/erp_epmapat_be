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

   // Busca Partidas de ingresos o Gastos (Para cálculos con todas las partidas)
   public List<Presupue> buscaPartidas(Integer tippar) {
      return dao.buscaPartidas(tippar);
   }

   public List<Presupue> findAllIng(String codpar, String nompar) {
      return dao.findAllIng(codpar, nompar);
   }

   // Busca por Código o Nombre
   public List<Presupue> findCodigoNombre(String codigoNombre) {
      return dao.findCodigoNombre(codigoNombre);
   }

   // Busca por codpar para datalist (OJO: Ya había, pero no se sabe si era por
   // codigo completo )
   public List<Presupue> findByCodpar(Long tippar, String codpar) {
      return dao.findByCodpar(tippar, codpar);
   }

   public List<Presupue> findByNompar(Long tippar, String nompar) {
      return dao.findByNompar(tippar, nompar);
   }

   public List<Presupue> findByTippar(Long tippar) {
      return dao.findByTippar(tippar);
   }

   public List<Presupue> buscaByCodigoI(String codpar) {
      return dao.buscaByCodigoI(codpar);
   }

   // Busca por Tipo, Código y Nombre
   public List<Presupue> findByTipoCodigoyNombre(Integer tippar, String codpar, String nompar) {
      return dao.findByTipoCodigoyNombre(tippar, codpar, nompar);
   }

   // Validar Código
   public List<Presupue> buscaByCodigo(String codpar) {
      return dao.buscaByCodpar(codpar);
   }

   // Partidas de un partida del clasificador
   public List<Presupue> buscaClasificador(String codigo) {
      return dao.buscaClasificador(codigo);
   }

   public <S extends Presupue> S save(S entity) {
      return dao.save(entity);
   }

   public List<Presupue> findAll() {
      return dao.findAll();
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

   // Usada en Ejecución Presupuestaria
   public Double totalCodpar(Long tippar, String codpar) {
      Double total = dao.totalCodpar(tippar, codpar);
      return total;
   }

   public List<Presupue> findByActividad(Long intest) {
      return dao.findByActividad(intest);
   }

}
