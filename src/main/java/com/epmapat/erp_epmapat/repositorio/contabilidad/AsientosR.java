package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Asientos;

public interface AsientosR extends JpaRepository<Asientos, Long> {

	// @Query(value = "SELECT * FROM asientos ORDER BY idasiento DESC LIMIT 10 ", nativeQuery = true)
	// public List<Asientos> findLastTen();

	// @Query(value = "SELECT * FROM asientos WHERE tipcom = (?1) and compro BETWEEN (?2) AND (?3)", nativeQuery = true)
	// public List<Asientos> findByNumero(Long tipo, Long desde, Long hasta);

	@Query(value = "SELECT * FROM asientos WHERE asiento BETWEEN (?1) AND (?2) and fecha BETWEEN (?3) AND (?4) ORDER BY asiento ASC", nativeQuery = true)
	public List<Asientos> findAsientos(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	@Query(value = "SELECT * FROM asientos WHERE tipcom BETWEEN (?1) AND (?2) and compro BETWEEN (?3) AND (?4) and fecha BETWEEN (?5) AND (?6) ORDER BY compro ASC", nativeQuery = true)
	public List<Asientos> findComprobantes(Integer tipcom1, Integer tipcom2, Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	// @Query(value = "SELECT * FROM asientos WHERE tipcom > ?1 and asiento BETWEEN (?2) AND (?3) and fecha BETWEEN (?4) AND (?5) ORDER BY asiento ASC", nativeQuery = true)
	// public List<Asientos> findTodos(Long tipo, Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	// @Query(value = "SELECT max(compro) FROM asientos WHERE tipcom = (?1) group by tipcom ORDER BY tipcom", nativeQuery = true)
	// public List<Asientos> findByCompro(Long tipcom);

	// @Query(value = "SELECT * FROM asientos WHERE fecha BETWEEN  (?3) AND (?4) OR compro BETWEEN (?1) AND (?2) ORDER BY asiento ASC", nativeQuery = true)
	// public List<Asientos> findByDorN(Long d_num, Long h_num, Date d_date, Date h_date);

	// Ultimo por Asiento
	Asientos findFirstByOrderByAsientoDesc();

	// Ultimo por Fecha
	@Query("SELECT MAX(a.fecha) FROM Asientos a")
	Date findUltimaFecha();

	// Ultimo comrobante
	@Query("SELECT MAX(a.compro) FROM Asientos a WHERE a.tipcom = :tipcom")
	Long findLastComproByTipcom(@Param("tipcom") Integer tipcom);

	Asientos findByTipcomAndCompro(Long tipcom, Long compro);

	// Un asiento
	Asientos findByIdasiento(Long idasiento);

	// Siguiente
	@Query(value = "SELECT * FROM asientos ORDER BY asiento DESC LIMIT 1", nativeQuery = true)
	Asientos findTopByOrderByNumeroDesc();

	// Valida Número de Comprobante
	@Query("SELECT COUNT(a) > 0 FROM Asientos a WHERE a.tipcom = :tipcom AND a.compro = :compro")
	boolean valComproOld(@Param("tipcom") Integer tipcom, @Param("compro") Long compro);

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
			@Param("idasiento") Long idasiento);

}
