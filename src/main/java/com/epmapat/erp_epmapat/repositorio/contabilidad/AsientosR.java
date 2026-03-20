package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;

public interface AsientosR extends JpaRepository<Asientos, Long> {

	// Asientos por números y fechas
	@Query("SELECT a FROM Asientos a WHERE a.asiento BETWEEN :desdeNum AND :hastaNum AND a.fecha BETWEEN :desdeFecha AND :hastaFecha ORDER BY a.asiento ASC")
	List<Asientos> findAsientos(@Param("desdeNum") Long desdeNum, 
										 @Param("hastaNum") Long hastaNum, 
										 @Param("desdeFecha") LocalDate desdeFecha, 
										 @Param("hastaFecha") LocalDate hastaFecha);
	
	// Comprobantes por números y fechas
	@Query("SELECT a FROM Asientos a WHERE a.tipcom = :tipcom AND a.compro BETWEEN :desdeNum AND :hastaNum AND a.fecha BETWEEN :desdeFecha AND :hastaFecha ORDER BY a.compro ASC")
	List<Asientos> findComprobantes(@Param("tipcom") Integer tipcom, 
											  @Param("desdeNum") Long desdeNum, 
											  @Param("hastaNum") Long hastaNum, 
											  @Param("desdeFecha") LocalDate desdeFecha, 
											  @Param("hastaFecha") LocalDate hastaFecha);
	
	// Ultimo por Asiento
	Asientos findFirstByOrderByAsientoDesc();

	// Ultimo por Fecha
	@Query("SELECT MAX(a.fecha) FROM Asientos a")
	LocalDate findUltimaFecha();

	// Ultimo comprobante
	@Query("SELECT MAX(a.compro) FROM Asientos a WHERE a.tipcom = :tipcom")
	Long findLastComproByTipcom(@Param("tipcom") Integer tipcom);

	Asientos findByTipcomAndCompro(Long tipcom, Long compro);

	// Un asiento
	Asientos findByIdasiento(Long idasiento);

	// Siguiente
	@Query(value = "SELECT * FROM asientos ORDER BY asiento DESC LIMIT 1", nativeQuery = true)
	Asientos findTopByOrderByNumeroDesc();

	// Valida Número de Comprobante
	// @Query("SELECT COUNT(a) > 0 FROM Asientos a WHERE a.tipcom = :tipcom AND a.compro = :compro")
	// boolean valComproOld(@Param("tipcom") Integer tipcom, @Param("compro") Long compro);

	@Query("SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END FROM Asientos a WHERE a.tipcom = :tipcom AND a.compro = :compro")
	boolean valCompro(@Param("tipcom") Integer tipcom, @Param("compro") Long compro);

	// Actualizar Totales del Asiento
	@Query("UPDATE Asientos a SET a.totdeb = :totdeb, a.totcre = :totcre WHERE a.idasiento = :idasiento")
	void updateTotales(@Param("totdeb") BigDecimal totdeb, @Param("totcre") BigDecimal totcre,
			@Param("idasiento") Long idasiento);

	@Modifying
	@Transactional
	@Query("UPDATE Asientos a SET a.totdeb = :totdeb, a.totcre = :totcre WHERE a.idasiento = :idasiento")
	void updateTotdebAndTotcre(@Param("totdeb") BigDecimal totdeb, @Param("totcre") BigDecimal totcre,
	@Param("idasiento") Long idasiento );

}
