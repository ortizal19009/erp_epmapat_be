package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;

public interface TransaciR extends JpaRepository<Transaci, Long> {

	// Bancos
	@Query("""
			SELECT t
			FROM Transaci t
			JOIN FETCH t.idasiento a
			LEFT JOIN FETCH t.idbene b
			WHERE t.idcuenta.idcuenta = ?1
			  AND t.mesconcili = ?2
			ORDER BY a.fecha ASC, t.orden ASC
			""")
	public List<Transaci> findMovibank(Long idcuenta, Integer mes);

	// Verifica si una Cuenta tiene Transacciones
	@Query("SELECT COUNT(t) > 0 FROM Transaci t WHERE t.codcue = :codcue")
	boolean tieneTransaci(@Param("codcue") String codcue);

	// Transacciones de un idasiento
	@Query("""
			SELECT t
			FROM Transaci t
			JOIN FETCH t.idasiento a
			LEFT JOIN FETCH t.idbene b
			WHERE a.idasiento = ?1
			ORDER BY t.orden ASC
			""")
	public List<Transaci> findTransaci(Long idasiento);

	// Asiento tiene transacciones
	@Query(value = "SELECT EXISTS (SELECT 1 FROM Transaci WHERE idasiento = ?1)", nativeQuery = true)
	boolean existsByIdasiento(Long idasiento);

	// Cuenta las transacciones de un asiento
	short countByIdasiento_Idasiento(Long idasiento);

	// Para el mayor
	@Query("""
			SELECT t
			FROM Transaci t
			JOIN FETCH t.idasiento a
			LEFT JOIN FETCH t.idbene b
			WHERE t.codcue LIKE CONCAT(?1, '%')
			  AND a.fecha >= ?2
			  AND a.fecha <= ?3
			ORDER BY a.fecha ASC, t.orden ASC
			""")
	public List<Transaci> findByCodcue(String codcue, LocalDate desde, LocalDate hasta);

	// Para Saldo Anterior
	@Query(value = "SELECT * FROM transaci t INNER JOIN asientos a ON t.idasiento = a.idasiento where t.codcue like ?1% and a.fecha<?2 order by a.fecha", nativeQuery = true)
	public List<Transaci> findByCodcueHasta(String codcue, LocalDate hasta);

	// Balance de Comproboación
	@Query(value = "select t.codcue, COALESCE(SUM(CASE WHEN (c.asiento = 1 and  t.debcre = 1) THEN valor END), 0) as deudor,COALESCE(SUM(CASE WHEN (c.asiento = 1 and  t.debcre = 2) THEN valor END), 0) as acreedor, COALESCE(SUM(CASE WHEN (c.asiento > 1 and  t.debcre = 1) THEN valor END), 0) as debito, COALESCE(SUM(CASE WHEN (c.asiento > 1 and  t.debcre = 2) THEN valor END), 0) as credito  from transaci AS t JOIN asientos AS c ON t.idasiento = c.idasiento where (c.fecha between (?1) and (?2) ) group by t.codcue order by t.codcue ", nativeQuery = true)
	List<Map<String, Object>> obtenerBalance(LocalDate desdeFecha, LocalDate hastaFecha);

	// Estado de Situacion y Resultados
	@Query(value = "select c.intgrupo, t.codcue, COALESCE(SUM(CASE WHEN t.debcre = 1 THEN valor END), 0) as debito, COALESCE(SUM(CASE WHEN t.debcre = 2 THEN valor END), 0) as credito  from asientos a, transaci t join cuentas c on t.idcuenta = c.idcuenta where (c.intgrupo = ?1) and t.idasiento = a.idasiento and (a.fecha BETWEEN (?2) AND (?3) ) group by c.intgrupo,t.codcue ", nativeQuery = true)
	List<Map<String, Object>> obtenerEstados(Long intgrupo, LocalDate desdeFecha, LocalDate hastaFecha);

	// Flujo del Efectivo
	@Query(value = "SELECT sum(valor) FROM transaci AS a JOIN asientos AS c ON a.idasiento = c.idasiento WHERE a.codcue like ?1% AND c.fecha BETWEEN (?2) AND (?3)  and a.debcre = ?4 and c.tipasi> 1", nativeQuery = true)
	Double totalFlujo(String codcue, LocalDate desdeFecha, LocalDate hastaFecha, Long debcre);

	// Suma valor (débitos o crédito)
	@Query(value = "SELECT SUM(t.valor) FROM Transaci t JOIN asientos a ON t.idasiento = a.idasiento WHERE t.codcue LIKE ?1% AND t.debcre=?2 and a.fecha>=?3 and a.fecha<=?4", nativeQuery = true)
	BigDecimal sumValor(String codcue, Integer debcre, LocalDate desde, LocalDate hasta);

	// Sinafip
	@Query(value = "SELECT * FROM transaci t JOIN asientos a ON  t.idasiento = a.idasiento WHERE a.tipasi = ?1", nativeQuery = true)
	public List<Transaci> findByTipAsi(Long tipasi);

	// Transaciones de los Asientos por números y fechas (Para el reporte de detalle
	// de asientos)
	@Query(value = "SELECT * FROM transaci t INNER JOIN asientos a ON t.idasiento = a.idasiento WHERE a.asiento BETWEEN (?1) AND (?2) and a.fecha BETWEEN (?3) AND (?4) ORDER BY a.asiento ASC", nativeQuery = true)
	public List<Transaci> tranAsientos(Long desdeAsi, Long hastaAsi, LocalDate desdeFecha, LocalDate hastaFecha);

	// === Para SaldoCuentas ===
	// Saldos Anteriores
	@Query("""
			    SELECT
			        SUM(CASE WHEN t.debcre = 1 THEN t.valor ELSE 0 END),
			        SUM(CASE WHEN t.debcre = 2 THEN t.valor ELSE 0 END)
			    FROM Transaci t
			    WHERE t.codcue = :codcue
			      AND t.idasiento.fecha < :desde
			""")
	List<Object[]> obtenerTotalesAnteriores(
			@Param("codcue") String codcue,
			@Param("desde") LocalDate desde);

	// Saldos del período
	@Query("""
			    SELECT
			        SUM(CASE WHEN t.debcre = 1 THEN t.valor ELSE 0 END),
			        SUM(CASE WHEN t.debcre = 2 THEN t.valor ELSE 0 END)
			    FROM Transaci t
			    WHERE t.codcue = :codcue
			      AND t.idasiento.fecha BETWEEN :desde AND :hasta
			""")
	List<Object[]> obtenerTotalesPeriodo(
			@Param("codcue") String codcue,
			@Param("desde") LocalDate desde,
			@Param("hasta") LocalDate hasta);

}
