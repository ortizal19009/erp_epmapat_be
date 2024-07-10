package com.epmapat.erp_epmapat.repositorio;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;

public interface RubroxfacR extends JpaRepository<Rubroxfac, Long> {

	// Rubros de una Planilla por Nro. de Factura
	// @Query(value="SELECT * FROM rubroxfac WHERE idfactura_facturas=?1",
	// nativeQuery=true)
	// public List<Rubroxfac> findByFactura(Long nrofactura);

	// Campos específicos: Rubro y Valor de una Factura (Planilla)
	@Query("SELECT new map(" +
			"r.descripcion as descripcion, " + "rf.valorunitario as valorunitario) " +
			"FROM Rubroxfac rf INNER JOIN Rubros r ON r.idrubro = rf.idrubro_rubros WHERE rf.idfactura_facturas=?1 order by rf.idrubro_rubros")
	List<Map<String, Object>> rubrosByIdfactura(Long idfactura);

	@Query(value = "select sum(valorunitario)  from rubroxfac r where idfactura_facturas = ?1", nativeQuery = true)
	Double findSuma(Long idfactura);

	/* Obtener suma de rubros x factura */
	@Query(value = "select sum(valorunitario) from rubroxfac rf join rubros r on rf.idrubro_rubros = r.idrubro where idfactura_facturas = ?1", nativeQuery = true)
	Double sumaRubros(Long idfactura);

	/*
	 * @Query(value =
	 * "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros"
	 * , nativeQuery = true)
	 */
	@Query(value = "select rf.idrubro_rubros , sum(rf.valorunitario) from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros ", nativeQuery = true)
	List<RubroxfacI> getByFechaCobro(Date d, Date h);

	@Query(value = "SELECT * FROM rubroxfac rf JOIN facturas f ON rf.idfactura_facturas = f.idfactura WHERE f.fechacobro between ?1 and ?2", nativeQuery = true)
	public List<Rubroxfac> findByFecha(Date d, Date h);

	/* SIN COBRO 2.0 */
	@Query(value = "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where totaltarifa > 0 and idcliente=?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaanulacion is null and f.fechaeliminacion is null ORDER BY f.idabonado, f.idfactura ", nativeQuery = true)
	public List<Rubroxfac> findSinCobroRF(Long cuenta);

	// Rubros de una Planilla
	@Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 order by idrubro_rubros", nativeQuery = true)
	public List<Rubroxfac> findByIdfactura(Long idfactura);

	// Rubros de una Planilla (Sin rubro 165 (Iva del siim 'esiva'))
	@Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 and idrubro_rubros <> 165 order by idrubro_rubros", nativeQuery = true)
	public List<Rubroxfac> findByIdfactura1(Long idfactura);

	// Rubroxfac de un Rubro (movimientos de un Rubro)
	@Query(value = "SELECT * FROM rubroxfac WHERE idrubro_rubros =?1 order by idrubroxfac desc limit 100", nativeQuery = true)
	public List<Rubroxfac> findByIdrubro(Long idrubro);

	// Rubro.descripcion y rubroxfac.valorunitario
	@Query(value = "SELECT r.descripcion, rf.valorunitario "
			+ "FROM rubroxfac rf "
			+ "INNER JOIN rubros r ON rf.idrubro_rubros = r.idrubro "
			+ "WHERE rf.idfactura_facturas = :idFactura", nativeQuery = true)
	List<Object[]> findRubros(@Param("idFactura") Long idFactura);

	// Multa en Planilla
	@Query(value = "SELECT EXISTS (SELECT 1 FROM Rubroxfac WHERE idrubro_rubros = 6 and idfactura_facturas = ?1)", nativeQuery = true)
	boolean findMulta(Long idfactura);

	@Query("SELECT r.idrubro, r.descripcion, SUM(rf.cantidad * rf.valorunitario) AS total FROM Rubroxfac rf INNER JOIN rf.idrubro_rubros r GROUP BY r.idrubro, r.descripcion")
	List<Object[]> findRubroTotalByRubroxfac();

	// Recaudcion diaria - Total por Rubro (Todas)
	@Query("SELECT r.idrubro, r.descripcion, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva FROM Rubroxfac rf "
			+
			"INNER JOIN rf.idrubro_rubros r " +
			"INNER JOIN rf.idfactura_facturas f " +
			"WHERE f.fechacobro = :fechacobro and not r.idrubro = 165 GROUP BY r.idrubro, r.descripcion")
	List<Object[]> findRubroTotalByRubroxfacAndFechacobro(@Param("fechacobro") LocalDate fechacobro);

	// Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva FROM Rubroxfac rf "
			+
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea <= ?3 AND NOT f.estado = 3 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL) "
			+
			"GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro")
	List<Object[]> totalRubrosAnteriorRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta);

	// Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva "
			+
			"FROM Rubroxfac rf " +
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea > ?3 AND NOT f.estado = 3 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL) "
			+
			"GROUP BY r.descripcion, r.idrubro " +
			"ORDER BY r.idrubro")
	List<Object[]> totalRubrosActualRangos(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta);

	// Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva FROM Rubroxfac rf "
			+
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea <= ?3 AND NOT f.estado = 3 AND f.usuariocobro = ?4 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL)"
			+
			"GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro")
	List<Object[]> totalRubrosAnteriorByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrec);

	// Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva "
			+
			"FROM Rubroxfac rf " +
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2 ) AND f.feccrea > ?3 AND NOT f.estado = 3 AND f.usuariocobro = ?4 AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL)"
			+
			"GROUP BY r.descripcion, r.idrubro " +
			"ORDER BY r.idrubro")
	List<Object[]> totalRubrosActualByRecaudador(LocalDate d_fecha, LocalDate h_fecha, LocalDate hasta, Long idrec);

	// Recaudcion diaria - Total por Rubros A.A. (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva  FROM Rubroxfac rf "
			+
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE date(f.fechacobro) = ?1 AND f.feccrea <= ?2 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL)"
			+
			"GROUP BY r.descripcion, r.idrubro ORDER BY r.idrubro")
	List<Object[]> totalRubrosAnterior(LocalDate fecha, LocalDate hasta);

	// Recaudcion diaria - Total por Rubro Año actual (Desde Facturas)
	@Query("SELECT r.idrubro, r.descripcion AS nombre_rubro, SUM(rf.cantidad * rf.valorunitario) AS total , r.swiva AS iva "
			+
			"FROM Rubroxfac rf " +
			"JOIN Facturas f ON f.idfactura = rf.idfactura_facturas " +
			"JOIN Rubros r ON r.idrubro = rf.idrubro_rubros " +
			"WHERE date(f.fechacobro) = ?1 AND f.feccrea > ?2 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <=?1 or f.fechaanulacion IS NULL)"
			+
			"GROUP BY r.descripcion, r.idrubro " +
			"ORDER BY r.idrubro")
	List<Object[]> totalRubrosActual(LocalDate fecha, LocalDate hasta);

	@Query(value = "select f.idfactura , sum((rf.cantidad * rf.valorunitario)*?1) as iva from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros = r.idrubro where idfactura = ?2 and r.swiva = true group by f.idfactura", nativeQuery = true)
	List<Object[]> getIva(BigDecimal iva, Long idfactura);

	/* FACTURACION ELECTRONICA */

	// @Query(value = "select rf from rubroxfac rf join facturas f on
	// rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros =
	// r.idrubro where f.idfactura = ?1 and not r.idrubro = 165 and f.pagado = 1",
	// nativeQuery = true)
	@Query(value = "select * from rubroxfac rf where rf.idfactura_facturas = ?1 and not rf.idrubro_rubros = 165 order by idrubro_rubros asc", nativeQuery = true)
	List<Rubroxfac> getRubrosByFactura(Long idfactura);

}
