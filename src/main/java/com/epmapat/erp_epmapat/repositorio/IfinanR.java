package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Ifinan;

public interface IfinanR extends JpaRepository<Ifinan, Long> {

   // Lista de Instituciones (Búsqueda por Código)
   @Query(value = "SELECT * FROM ifinan WHERE codifinan LIKE ?1% order by codifinan", nativeQuery = true)
   List<Ifinan> findByCodifinan(String codifinan);

   // Lista de Instituciones (Búsqueda por Nombre)
   @Query(value = "SELECT * FROM ifinan WHERE LOWER(nomifinan) LIKE %?1% order by nomifinan", nativeQuery = true)
   List<Ifinan> findByNomifinan(String nomifinan);

}
