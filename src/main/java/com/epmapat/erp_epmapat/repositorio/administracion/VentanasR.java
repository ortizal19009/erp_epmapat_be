package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;

public interface VentanasR extends JpaRepository<Ventanas, Long> {

   @Query("SELECT v FROM Ventanas v WHERE v.idusuario = :idusuario AND v.nombre = :nombre")
   Ventanas findByIdusuarioAndNombre(@Param("idusuario") Long idusuario, @Param("nombre") String nombre);

   @Query("""
         SELECT v
         FROM Ventanas v
         WHERE v.idusuario = :idusuario
           AND LOWER(TRIM(v.nombre)) = LOWER(TRIM(:nombre))
         ORDER BY v.idventana ASC
         """)
   List<Ventanas> findByIdusuarioAndNombreNormalizado(
         @Param("idusuario") Long idusuario,
         @Param("nombre") String nombre);

   List<Ventanas> findByIdusuarioOrderByNombreAsc(Long idusuario);

   @Query("SELECT DISTINCT v.nombre FROM Ventanas v WHERE v.nombre IS NOT NULL AND TRIM(v.nombre) <> '' ORDER BY v.nombre")
   List<String> findDistinctNombres();

}
