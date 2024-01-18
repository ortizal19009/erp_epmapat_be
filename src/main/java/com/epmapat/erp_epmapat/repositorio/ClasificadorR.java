package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.jpa.repository.Query;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import com.epmapat.erp_epmapat.modelo.Clasificador;

public interface ClasificadorR extends JpaRepository<Clasificador, Long> {

   @Query(value = "SELECT * FROM clasificador where codpar like ?1% order by codpar", nativeQuery = true)
   List<Clasificador> buscaByCodigo(String codpar);

   @Query(value = "SELECT * FROM clasificador where LOWER(nompar) like %?1% order by codpar", nativeQuery = true)
   List<Clasificador> buscaByNombre(String nompar);

   // Partidas de Ingreso por Codpar o Nompar
   @Query(value = "SELECT * FROM clasificador where codpar < '4' and nivpar=4 and codpar like ?1% and LOWER(nompar) like %?2% order by codpar", nativeQuery = true)
   List<Clasificador> buscaParingreso(String codpar, String nompar);

   // Partidas de Gastos por Código o Nombre (En un solo campo)
   @Query(value = "SELECT * FROM clasificador where codpar > '4' and nivpar=4 and (codpar like %?1% or LOWER(nompar) like %?1%) order by codpar", nativeQuery = true)
   List<Clasificador> findPartidasG( String codigoNombre );

}
