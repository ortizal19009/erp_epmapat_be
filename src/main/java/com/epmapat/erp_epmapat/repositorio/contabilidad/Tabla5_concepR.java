package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tabla5_concep;

public interface Tabla5_concepR extends JpaRepository<Tabla5_concep, Long> {

   // tipoiva B = Bienes
   @Query("SELECT tc FROM Tabla5_concep tc JOIN FETCH tc.idtabla5 t WHERE t.tipoiva = 'B' order by tc.casillero104")
   List<Tabla5_concep> findByTipoIvaB();

   // tipoiva S = Servicios
   @Query("SELECT tc FROM Tabla5_concep tc JOIN FETCH tc.idtabla5 t WHERE t.tipoiva = 'S' order by tc.casillero104")
   List<Tabla5_concep> findByTipoIvaS();

   // codporcentaje 3 = 100%
   @Query("SELECT tc FROM Tabla5_concep tc JOIN FETCH tc.idtabla5 t WHERE t.codporcentaje = '3' order by tc.casillero104")
   List<Tabla5_concep> findByCodporcentaje();

}
