package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;

public interface PresupueR extends JpaRepository<Presupue, Long> {

   // Todas las Partidas de Ingresos
   @Query(value = "SELECT * FROM presupue where tippar = 1 and codpar like ?1% and LOWER(nompar) like %?2% order by codpar", nativeQuery = true)
   List<Presupue> findAllIng(String codpar, String nompar);

   // Partidas de Ingresos por Codigo o Nombre
   @Query(value = "SELECT * FROM presupue where tippar = 1 and ( LOWER(nompar) like %?1% or codpar like %?1% ) order by codpar", nativeQuery = true)
   List<Presupue> findCodigoNombre(String codigoNombre);

   @Query(value = "SELECT * FROM presupue where (tippar = 1) and (codpar like ?1%) order by codpar", nativeQuery = true)
   List<Presupue> buscaByCodigoI(String codpar);

   // Creo que no se usa?
   @Query(value = "SELECT * FROM presupue where tippar = 1 and (LOWER(nompar) like %?1%) order by codpar", nativeQuery = true)
   List<Presupue> buscaByNombreI(String nompar);

   // Partidas por Tipo, Codigo y Nombre
   @Query(value = "SELECT * FROM presupue where tippar = ?1 and codpar like %?2% and LOWER(nompar) like %?3% order by codpar", nativeQuery = true)
   List<Presupue> findByTipoCodigoyNombre(Integer tippar, String codpar, String nompar);

   // Partidas de Gastos por Código
   // @Query(value = "SELECT * FROM presupue where (tippar = 2) and (codpar like
   // %?1%) order by codpar", nativeQuery = true)
   // List<Presupue> buscaByCodigoG(String codpar);

   // Partidas de Gastos por Nombre
   // @Query(value = "SELECT * FROM presupue where tippar = 2 and (LOWER(nompar)
   // like %?1%) order by codpar", nativeQuery = true)
   // List<Presupue> buscaByNombreG(String nompar);

   @Query(value = "SELECT * FROM presupue where tippar = 2 and codpar = ?1", nativeQuery = true)
   List<Presupue> buscaByCodpar(String codpar);

   // Cuenta las Partidas por Actividad
   Long countByIdestrfunc(Long idestrfunc);

   // Partidas por Actividad
   @Query("SELECT p FROM Presupue p WHERE p.idestrfunc = :idestrfunc ORDER BY codpar")
   List<Presupue> findByActividad(@Param("idestrfunc") Long idestrfunc);

}
