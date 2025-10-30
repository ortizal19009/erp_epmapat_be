package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Estrfunc;

public interface EstrfuncR extends JpaRepository<Estrfunc, Long> {

   // Para validar por Código
   @Query(value = "SELECT * FROM estrfunc where codigo=?1", nativeQuery = true)
   List<Estrfunc> findByCodigo(String codigo);

   // Para validar por Nombre
   @Query(value = "SELECT * FROM estrfunc where LOWER(nombre)=?1", nativeQuery = true)
   List<Estrfunc> findByNombre(String nombre);

   // Actividades por Codigo o Nombre
   @Query(value = "SELECT * FROM estrfunc where (codigo like %?1% or LOWER(nombre) like %?1%) and codigo <> '00' order by codigo", nativeQuery = true)
   List<Estrfunc> findCodigoNombre(String codigoN6ombre);

}
