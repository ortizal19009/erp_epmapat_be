package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Pagoscobros;

public interface PagoscobrosR extends JpaRepository<Pagoscobros, Long> {

   // Suma pagoscobros.valr para actualizar benextran.totpagcob
   @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Pagoscobros p WHERE p.idbenxtra.idbenxtra = :idbenxtra")
   BigDecimal totalPagosPorBenxtra(@Param("idbenxtra") Long idbenxtra);

   // Pagoscobros de una transaci.inttra
   List<Pagoscobros> findByInttra_Inttra(Long inttra);

   // Pagoscobros de un benextran.idbenxtar
   List<Pagoscobros> findByIdbenxtra_Idbenxtra(Long idbenxtra);

   // Cuenta los Pagoscobros de una transaci.inttra
   short countByInttra_Inttra(Long inttra);
}