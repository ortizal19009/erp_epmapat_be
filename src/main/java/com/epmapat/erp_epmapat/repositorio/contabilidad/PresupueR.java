package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;

public interface PresupueR extends JpaRepository<Presupue, Long> {

   //Partidas de Ingreso o Gasto (Para cálculos con todas las partidas)
   @Query(value = "SELECT * FROM presupue where tippar = ?1 order by codpar", nativeQuery = true)
   List<Presupue> buscaPartidas(Number tippar);

   //Partidas de Ingresos por Codigo Y Nombre
   @Query(value = "SELECT * FROM presupue where tippar = 1 and codpar like ?1% and LOWER(nompar) like %?2% order by codpar", nativeQuery = true)
   List<Presupue> findAllIng(String codpar, String nompar);

   // Partidas de Ingresos por Codigo O Nombre
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

   @Query(value = "SELECT * FROM presupue where (tippar = ?1) and (codpar like ?2%) order by codpar", nativeQuery = true)
   List<Presupue> findByCodpar(Long tippar, String codpar);
   @Query(value = "SELECT * FROM presupue where tippar = ?1 and (LOWER(nompar) like %?2%) order by codpar", nativeQuery = true)
   List<Presupue> findByNompar(Long tippar, String nompar);
   @Query(value = "SELECT * FROM presupue where (tippar = ?1) order by codpar", nativeQuery = true)
   List<Presupue> findByTippar(Long tippar);
   
   //Partidas de un partida del clasificador
   @Query(value = "SELECT * FROM presupue where codigo like ?1% order by codpar", nativeQuery = true)
   List<Presupue> buscaClasificador(String codigo);

   @Query(value = "SELECT * FROM presupue where tippar = 2 and codpar = ?1", nativeQuery = true)
   List<Presupue> buscaByCodpar(String codpar);

   //Busca por codpar para los datalist
   @Query(value = "SELECT * FROM presupue where tippar=?1 and codpar like ?2%", nativeQuery = true)
   List<Presupue> findByCodpar(Integer tippar, String codpar);

   // Cuenta las Partidas por Actividad
   // Long countByIdestrfunc(Long intest);

   // Partidas por Actividad
    @Query(value = "SELECT * FROM presupue p WHERE p.intest = ?1 ORDER BY p.codpar", nativeQuery= true)
    List<Presupue> findByActividad(Long intest);

   @Query(value = "SELECT sum(inicia) FROM presupue where tippar = ?1 AND codpar LIKE ?2%", nativeQuery = true)
   Double totalCodpar(Long tippar, String codpar);
 
}
