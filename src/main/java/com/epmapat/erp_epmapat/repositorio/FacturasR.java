package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.FacSinCobrar;
import com.epmapat.erp_epmapat.interfaces.FacturasI;
import com.epmapat.erp_epmapat.interfaces.RepFacGlobal;
import com.epmapat.erp_epmapat.modelo.Facturas;

// @Repository
public interface FacturasR extends JpaRepository<Facturas, Long> {

	// VALIDACION DE LA ULTIMA FACTURA DEL RECAUDADOR
	@Query(value = "select *, substring(nrofactura, 9) as nrofac from facturas where nrofactura like %?1% and not nrofactura  is null order by nrofac desc limit 1;", nativeQuery = true)
	public Facturas validarUltimafactura(String codrecaudador);

	@Query(nativeQuery = true, value = "select * from facturas f where f.usuariocobro = ?1 and (f.fechacobro between ?2 and ?3)")
	public List<Facturas> findByUsucobro(Long idusuario, Date dfecha, Date hfecha);

	@Query(nativeQuery = true, value = "select * from facturas f where f.fechacobro = ?1 ")
	public List<FacturasI> findByFechacobro(Date fechacobro);

	@SuppressWarnings("null")
	@Query(value = "SELECT * FROM facturas order by idfactura DESC LIMIT 12", nativeQuery = true)
	public List<Facturas> findAll();

	@Query(value = "SELECT * FROM facturas AS f WHERE f.idfactura >= ?1 and f.idfactura <= ?2 ", nativeQuery = true)
	public List<Facturas> findDesde(Long desde, Long hasta);

	// Planillas por Cliente
	@Query(value = "SELECT * FROM facturas WHERE idcliente=?1 ORDER BY idfactura DESC LIMIT 12", nativeQuery = true)
	public List<Facturas> findByIdcliente(Long idcliente);

	// 15 Planillas de un Abonado
	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY idfactura DESC LIMIT 15", nativeQuery = true)
	public List<Facturas> findByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM facturas f WHERE f.idabonado=?1 and f.fechaeliminacion is null ORDER BY idfactura DESC LIMIT ?2", nativeQuery = true)
	public List<Facturas> findByIdabonadoLimit(Long idabonado, Long limit);

	// Una Planilla (como lista para mostrar en la misma forma que por Abonado)
	public List<Facturas> findByIdfactura(Long idfactura);

	// Planillas por Abonado y Fecha
	@Query("SELECT f FROM Facturas f WHERE f.idabonado = :idabonado AND f.feccrea BETWEEN :fechaDesde AND :fechaHasta AND totaltarifa > 0 order by feccrea desc")
	List<Facturas> findByAbonadoAndFechaCreacionRange(@Param("idabonado") Long idabonado,
			@Param("fechaDesde") LocalDate fechaDesde, @Param("fechaHasta") LocalDate fechaHasta);

	// Planillas por Cliente (sinCobrar)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null ORDER BY idabonado, idfactura", nativeQuery = true)
	public List<Facturas> findSinCobro(Long idcliente);

	// Planillas sin cobrar por cliente valor a pagar calculado por la suma de los
	// rubros
	@Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.totaltarifa > 0 and f.idcliente= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165  group by f.idfactura ORDER BY f.idabonado asc, f.feccrea asc", nativeQuery = true)
	public List<FacSinCobrar> findFacSincobro(Long idcliente);

	// Cartera a una fecha
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0  and (( (estado = 1 or estado = 2) and ( fechacobro>?1 or fechacobro is null)) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null ORDER BY idabonado, idfactura", nativeQuery = true)
	public List<Facturas> cartera(LocalDate hasta);

	// Cartera de un cliente a una fecha (Facturas)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and feccrea<=?2 and (( (estado = 1 or estado = 2) and ( fechacobro>?2 or fechacobro is null)) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null ORDER BY idabonado, idfactura", nativeQuery = true)
	public List<Facturas> carteraCliente(Long idcliente, LocalDate hasta);

	// Cartera por cliente a una fecha (Total, ya suma 1 a los del módulo 3)
	// @Query(value = "SELECT SUM(CASE WHEN f.idmodulo.id = 3 THEN f.totaltarifa + 1
	// ELSE f.totaltarifa END) AS totalGeneral FROM facturas f JOIN f.clientes c
	// WHERE c.idcliente = :idcliente")
	@Query(value = "SELECT SUM(CASE WHEN idmodulo = 3 THEN totaltarifa + 1 ELSE totaltarifa END) AS totalGeneral FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and feccrea<=?2 and (( (estado = 1 or estado = 2) and ( fechacobro>?2 or fechacobro is null)) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null", nativeQuery = true)
	Double totCarteraCliente(@Param("idcliente") Long idcliente, LocalDate hasta);

	// Planillas por Abonado
	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY nrofactura", nativeQuery = true)
	public List<Facturas> findByIdFactura(Long idabonado);

	// Planilla por nrofactura
	@Query(value = "SELECT * FROM facturas WHERE nrofactura=?1 order by idfactura", nativeQuery = true)
	public List<Facturas> findByNrofactura(String nrofactura);

	// ID de las Planillas Sin cobrar por Abonado (para Multas)
	@Query(value = "SELECT idfactura FROM facturas WHERE totaltarifa > 0 and idabonado=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null and fechaconvenio is null ORDER BY idfactura", nativeQuery = true)
	public List<Long> findSinCobroAbo(Long idabonado);

	// Planillas Sin cobrar por modulo y Abonado (para Convenios)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idmodulo=?1 and idabonado=?2 and estado = 1 and fechacobro is null and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idfactura", nativeQuery = true)
	public List<Facturas> findSinCobrarAbo(Long idmodulo, Long idabonado);

	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and (idmodulo = 3 or idmodulo = 4) and idabonado=?1 and estado = 1 and fechacobro is null and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idfactura", nativeQuery = true)
	public List<Facturas> findSinCobrarAboMod(Long idabonado);

	@Query(value = "SELECT count (*) FROM facturas WHERE totaltarifa > 0 and (idmodulo = 3 or idmodulo = 4) and idabonado=?1 and estado = 1 and fechacobro is null and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ", nativeQuery = true)
	public Long countSinCobrarAbo(Long idabonado);
	// Recaudacion diaria - Facturas cobradas <Facturas>
	// @Query(value = "SELECT * FROM facturas WHERE (fechacobro = ?1 or
	// fechatransferencia=?1) and fechaanulacion is null and fechaeliminacion is
	// null ORDER BY idfactura", nativeQuery = true)
	// public List<Facturas> findByFechacobro(LocalDate fecha);
	// Recaudacion diaria - Facturas cobradas
	/*
	 * ========================================= QUERIS PARA RETPORTES
	 * ===========================================
	 */

	/*
	 * GLOBALES
	 */
	// Recaudacion diaria - Facturas cobradas

	/* @Query("SELECT f, SUM(rf.cantidad * rf.valorunitario)AS total, f.swiva FROM Rubroxfac rf "
			+ "JOIN Facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE date(f.fechacobro) = ?1 AND (f.estado = 1 or f.estado = 2) AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165  "
			+ "GROUP BY f.idfactura, f.nrofactura  ORDER BY f.idfactura")
	List<Object[]> _findByFechacobroTot(LocalDate fecha); */

	@Query(value = "SELECT f.idfactura AS idfactura, "
			+ "SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , f.swiva as iva "
			+ "FROM rubroxfac rf " + "JOIN facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE date(f.fechacobro) = ?1 " + "AND (f.estado = 1 OR f.estado = 2) "
			+ "AND f.fechaeliminacion IS NULL " + "AND rf.idrubro_rubros != 165 " + "GROUP BY f.idfactura ORDER BY f.nrofactura",

			nativeQuery = true)
	List<RepFacGlobal> findByFechacobroTot(LocalDate fecha);

	/*
	 * @Query(value =
	 * "SELECT f as facturas, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END )AS total FROM rubroxfac rf JOIN facturas f ON rf.idfactura_facturas = f.idfactura WHERE date(f.fechacobro) = ?1 AND (f.estado = 1 or f.estado = 2) AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165 GROUP BY f.idfactura, f.nrofactura  ORDER BY f.idfactura"
	 * , nativeQuery = true)
	 */

	/*
	 * @Query("SELECT f, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f "
	 * + "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura " +
	 * "WHERE date(f.fechacobro) = ?1 AND (f.estado = 1 or f.estado = 2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <= ?1 or f.fechaanulacion IS NULL)"
	 * + "GROUP BY f.idfactura, f.nrofactura  ORDER BY f.idfactura") List<Object[]>
	 * findByFechacobroTot(LocalDate fecha);
	 */

	// Total diario por Forma de cobro
	@Query(value = "SELECT fc.descripcion AS formaCobro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total FROM Rubroxfac rf "
			+ "JOIN Facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "JOIN Formacobro fc ON fc.idformacobro = f.formapago "
			+ "WHERE f.fechacobro = ?1 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165 "
			+ " GROUP BY fc.descripcion ORDER BY fc.descripcion", nativeQuery = true)
	List<Object[]> totalFechaFormacobro(@Param("fecha") LocalDate fecha);
	/*
	 * @Query(value =
	 * "SELECT fc.descripcion AS formaCobro, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f "
	 * + "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura " +
	 * "JOIN Formacobro fc ON fc.idformacobro = f.formapago " +
	 * "WHERE f.fechacobro = ?1 AND (f.estado=1 OR f.estado=2) AND f.fechaeliminacion IS NULL AND (f.fechaanulacion <= ?1 or f.fechaanulacion IS NULL)"
	 * + " GROUP BY fc.descripcion ORDER BY fc.descripcion") List<Object[]>
	 * totalFechaFormacobro(@Param("fecha") LocalDate fecha);
	 */

	/*
	 * POR RANGOS
	 */
	// Total diario por Forma de cobro
	@Query(value = "SELECT fc.descripcion AS formaCobro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total FROM Facturas f "
			+ "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura "
			+ "JOIN Formacobro fc ON fc.idformacobro = f.formapago "
			+ "WHERE (f.fechacobro BETWEEN ?1 and ?2) AND NOT f.estado = 3  AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165  GROUP BY fc.descripcion ORDER BY fc.descripcion", nativeQuery = true )
	List<Object[]> totalFechaFormacobroRangos(@Param("d_fecha") LocalDate d_fecha, @Param("d_fecha") LocalDate h_fecha);
	
	
	
	@Query(value = "SELECT f.idfactura AS idfactura, "
			+ "SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , f.swiva as iva "
			+ "FROM rubroxfac rf " + "JOIN facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2)" + "AND (f.estado = 1 OR f.estado = 2) "
			+ "AND f.fechaeliminacion IS NULL " + "AND rf.idrubro_rubros != 165 " + "GROUP BY f.idfactura ORDER BY f.nrofactura",
			nativeQuery = true)
	List<RepFacGlobal> findByFechacobroTotRangos(LocalDate d_fecha, LocalDate h_fecha);

	/*
	 * POR RECAUDADOR CON RANGO
	 */
	@Query(value = "SELECT  f.idfactura AS idfactura, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total ,f.swiva as iva FROM Facturas f "
			+ "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2) AND NOT f.estado = 3 AND f.usuariocobro = ?3 AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165 "
			+ "GROUP BY f.idfactura ORDER BY f.nrofactura", nativeQuery = true)
	List<RepFacGlobal> findByFechacobroTotByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador);

	@Query(value = "SELECT fc.descripcion AS formaCobro, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total FROM Facturas f "
			+ "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura "
			+ "JOIN Formacobro fc ON fc.idformacobro = f.formapago "
			+ "WHERE (f.fechacobro BETWEEN ?1 and ?2) AND NOT f.estado = 3 AND f.usuariocobro = ?3 AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165  GROUP BY fc.descripcion  ORDER BY fc.descripcion", nativeQuery = true)
	List<Object[]> totalFechaFormacobroByRecaudador(@Param("d_fecha") LocalDate d_fecha,
			@Param("d_fecha") LocalDate h_fecha, @Param("recaudador") Long idrecaudador);

	// Cuenta las Facturas pendientes de un Abonado
	@Query("SELECT COUNT(*) FROM Facturas f WHERE f.totaltarifa > 0 and f.idabonado=?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) AND f.fechaeliminacion IS NULL")
	long countFacturasByAbonadoAndPendientes(@Param("idabonado") Long idabonado);

	// Listado de facturas anuladas
	@Query(value = " select * from facturas f where not f.fechaanulacion is null  and  not f.usuarioanulacion  is null order by f.fechaanulacion desc limit ?1", nativeQuery = true)
	public List<Facturas> fingAllFacturasAnuladas(Long limit);

	@Query(value = "select * from facturas f where f.fechaanulacion between ?1 and ?2", nativeQuery = true)
	public List<Facturas> findByFecAnulacion(Date d, Date h);

	@Query(value = "select * from facturas f join clientes c on f.idcliente = c.idcliente where f.pagado = 1 and f.usuarioanulacion is null and f.fechaeliminacion is null and f.idcliente = ?1 order by f.feccrea desc", nativeQuery = true)
	public List<Facturas> findCobradasByCliente(Long idcliente);

	// Listado de facturas eliminadas
	@Query(value = " select * from facturas f where not f.fechaeliminacion is null  and  not f.usuarioeliminacion  is null order by f.fechaeliminacion desc limit ?1", nativeQuery = true)
	public List<Facturas> fingAllFacturasEliminadas(Long limit);

	@Query(value = "select * from facturas f where f.fechaeliminacion between ?1 and ?2", nativeQuery = true)
	public List<Facturas> findByFecEliminacion(Date d, Date h);

	/* reporte de facturas cobradas por transferencia */
	@Query(value = "select f, sum(rf.cantidad * rf.valorunitario) from Rubroxfac rf join Facturas f on rf.idfactura_facturas = f.idfactura where f.formapago = 4 and date(f.fechacobro) between ?1 and ?2 and f.pagado = 1 and f.estado = 1 and not rf.idrubro_rubros = 165  group by f.idfactura order by f.nrofactura asc")
	public List<Object[]> transferenciasCobradas(Date d_fecha, Date h_fecha);

	/* REPORTE DE FACTURAS TRANSFERIDAS PERO NO COBRADAS */
	@Query(value = "select f, sum(rf.cantidad * rf.valorunitario) from Rubroxfac rf join Facturas f on rf.idfactura_facturas = f.idfactura where f.formapago = 4 and date(f.fechacobro) between ?1 and ?2 and f.pagado = 1 and f.estado = 1 group by f.idfactura order by f.nrofactura asc")
	public List<Object[]> transferenciasNoCobradas(Date d_fecha, Date h_fecha);

	/* REPORTE PARA ANULACIONES Y BAJAS */

	/* COBRADAS POR RANGO */
	@Query(value = "select * from facturas f where f.fechacobro between ?1 and ?2 and f.pagado = 1 and f.fechaeliminacion is null", nativeQuery = true)
	public List<Facturas> findFechaCobro(LocalDate d, LocalDate h);

}
