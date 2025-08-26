package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import com.epmapat.erp_epmapat.interfaces.*;

import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Facturas;

@Repository
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
	@Query(value = "SELECT * FROM facturas WHERE idcliente=?1 and totaltarifa > 0 ORDER BY idfactura DESC LIMIT ?2", nativeQuery = true)
	public List<Facturas> findByIdcliente(Long idcliente, Long limit);

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
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null ORDER BY idfactura", nativeQuery = true)
	public List<Facturas> findSinCobro(Long idcliente);

	// Planillas por Cliente (sinCobrar para cartera vencida)
	@Query(value = """
			   select f.idfactura as factura,
			          m.descripcion as modulo,
				   c.nombre,
			          sum(rf.cantidad * rf.valorunitario) as total,
			          f.idabonado as cuenta,
			          f.feccrea
			   from facturas f
			   join rubroxfac rf on f.idfactura = rf.idfactura_facturas
			   join modulos m on f.idmodulo = m.idmodulo
			join clientes c on f.idcliente = c.idcliente
			   where f.totaltarifa > 0
			     and f.idcliente = ?1
			     and f.feccrea <= ?2
			     and (
			           (f.estado = 1 or f.estado = 2)
			           and (f.fechacobro is null or f.fechacobro > ?2)
			           or f.estado = 3
			         )
			     and f.fechaconvenio is null
			     and f.fechaeliminacion is null and rf.idrubro_rubros not in (79, 5, 165)
			   group by f.idfactura, m.descripcion, f.idabonado, f.feccrea, c.nombre
			   order by f.idfactura
			   """, nativeQuery = true)
	List<CVFacturasNoConsumo> SinCobroOfCV(Long idcliente, LocalDate fechacobro);

	// Planillas sin cobrar por cliente valor a pagar calculado por la suma de los
	// rubros
	@Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.totaltarifa > 0 and f.idcliente= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165  group by f.idfactura ORDER BY f.idabonado asc, f.feccrea asc", nativeQuery = true)
	public List<FacSinCobrar> findFacSincobro(Long idcliente);

	@Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.totaltarifa > 0 and f.idabonado= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165  group by f.idfactura ORDER BY f.idabonado asc, f.feccrea asc", nativeQuery = true)
	public List<FacSinCobrar> findFacSincobroByCuetna(Long cuenta);

	/* OPCION SOLO PARA REALIZAR EL CAMBIO DE PROPIETARIO */
	@Query(value = "select f.idfactura, f.idmodulo, f.totaltarifa AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar from facturas f where f.idabonado= ?1 and (( f.estado = 1 or f.estado = 2) and f.fechacobro is null) and f.fechaeliminacion is null and fechaconvenio is null ORDER BY f.idabonado asc, f.idfactura desc", nativeQuery = true)
	public List<FacSinCobrar> findByCuenta(Long cuenta);

	@Query(value = "select f.idfactura, f.idmodulo, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) AS total, f.idcliente, f.idabonado , f.feccrea, f.formapago, f.estado , f.pagado, f.swcondonar, m.descripcion as modulo from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join modulos m on f.idmodulo = m.idmodulo where f.totaltarifa > 0 and f.idabonado= ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaeliminacion is null and fechaconvenio is null and not rf.idrubro_rubros = 165  group by f.idfactura, m.descripcion ORDER BY f.idabonado asc, f.feccrea asc", nativeQuery = true)
	public List<FacSinCobrar> findSincobroByCuetna(Long cuenta);

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
	// @Query(value = "SELECT SUM(CASE WHEN idmodulo = 3 THEN totaltarifa + 1 ELSE
	// totaltarifa END) AS totalGeneral FROM facturas WHERE totaltarifa > 0 and
	// idcliente=?1 and feccrea<=?2 and (( (estado = 1 or estado = 2) and (
	// fechacobro>?2 or fechacobro is null)) or estado = 3 ) and fechaconvenio is
	// null and fechaeliminacion is null", nativeQuery = true)
	@Query(value = "SELECT SUM(rf.cantidad * rf.valorunitario) AS totalGeneral FROM facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas join rubros r on r.idrubro = rf.idrubro_rubros WHERE f.totaltarifa > 0 and f.idcliente = ?1 and (( (f.estado = 1 or f.estado = 2) and ( f.fechacobro> ?2 or f.fechacobro is null)) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is null and not r.idrubro = 165 and not r.idrubro = 5", nativeQuery = true)
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

		@Query(value = "SELECT idfactura FROM facturas WHERE totaltarifa > 0 and idabonado=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null and fechaconvenio is null ORDER BY idfactura", nativeQuery = true)
	public List<Long> coutPendientes(Long idabonado);

	// Planillas Sin cobrar por modulo y Abonado (para Convenios)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idmodulo=?1 and idabonado=?2 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null and fechaconvenio is null ORDER BY idfactura", nativeQuery = true)
	public List<Facturas> findSinCobrarAbo(Long idmodulo, Long idabonado);

	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and (idmodulo = 3 or idmodulo = 4) and idabonado=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaeliminacion is null and fechaconvenio is null ORDER BY idfactura", nativeQuery = true)
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

	/*
	 * @Query("SELECT f, SUM(rf.cantidad * rf.valorunitario)AS total, f.swiva FROM Rubroxfac rf "
	 * + "JOIN Facturas f ON rf.idfactura_facturas = f.idfactura "
	 * +
	 * "WHERE date(f.fechacobro) = ?1 AND (f.estado = 1 or f.estado = 2) AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165  "
	 * + "GROUP BY f.idfactura, f.nrofactura  ORDER BY f.idfactura")
	 * List<Object[]> _findByFechacobroTot(LocalDate fecha);
	 */

	@Query(value = "SELECT f.idfactura AS idfactura, "
			+ "SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , f.swiva as iva "
			+ "FROM rubroxfac rf " + "JOIN facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE date(f.fechacobro) = ?1 " + "AND (f.estado = 1 OR f.estado = 2) "
			+ "AND f.fechaeliminacion IS NULL " + "AND rf.idrubro_rubros != 165 "
			+ "GROUP BY f.idfactura ORDER BY f.nrofactura",

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
			+ "WHERE (f.fechacobro BETWEEN ?1 and ?2) AND NOT f.estado = 3  AND f.fechaeliminacion IS NULL AND not rf.idrubro_rubros = 165  GROUP BY fc.descripcion ORDER BY fc.descripcion", nativeQuery = true)
	List<Object[]> totalFechaFormacobroRangos(@Param("d_fecha") LocalDate d_fecha, @Param("d_fecha") LocalDate h_fecha);

	@Query(value = "SELECT f.idfactura AS idfactura, "
			+ "SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END) AS total , f.swiva as iva "
			+ "FROM rubroxfac rf JOIN facturas f ON rf.idfactura_facturas = f.idfactura "
			+ "WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2) AND (f.estado = 1 OR f.estado = 2) "
			+ "AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros != 165 "
			+ "GROUP BY f.idfactura ORDER BY f.nrofactura", nativeQuery = true)
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
	@Query("SELECT COUNT(*) FROM Facturas f WHERE f.totaltarifa > 0 and f.idabonado=?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) AND f.fechaeliminacion IS NULL and not f.idmodulo = 27 and fechaconvenio is null")
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
	@Query(value = "select f.idfactura, f.fechatransferencia, f.nrofactura, f.formapago, SUM(CASE WHEN f.swcondonar = true AND rf.idrubro_rubros = 6 THEN 0 ELSE rf.valorunitario * rf.cantidad END ) as valor, c.nombre, c.cedula, f.swiva from Rubroxfac rf join Facturas f on rf.idfactura_facturas = f.idfactura join clientes c on f.idcliente = c.idcliente where f.formapago = 4 and date(f.fechacobro) between ?1 and ?2 and f.pagado = 1 and f.estado = 1 and not rf.idrubro_rubros = 165  group by f.idfactura, c.nombre, c.cedula order by f.nrofactura asc", nativeQuery = true)
	public List<R_transferencias> transferenciasCobradas(Date d_fecha, Date h_fecha);

	/* REPORTE DE FACTURAS TRANSFERIDAS PERO NO COBRADAS */
	@Query(value = "select f, sum(rf.cantidad * rf.valorunitario) from Rubroxfac rf join Facturas f on rf.idfactura_facturas = f.idfactura where f.formapago = 4 and date(f.fechacobro) between ?1 and ?2 and f.pagado = 1 and f.estado = 1 group by f.idfactura order by f.nrofactura asc")
	public List<Object[]> transferenciasNoCobradas(Date d_fecha, Date h_fecha);

	/* REPORTE PARA ANULACIONES Y BAJAS */

	/* COBRADAS POR RANGO */
	@Query(value = "select * from facturas f where f.fechacobro between ?1 and ?2 and f.pagado = 1 and f.fechaeliminacion is null", nativeQuery = true)
	public List<Facturas> findFechaCobro(LocalDate d, LocalDate h);

	/* REPORTE DE FACTURAS ELIMINADAS POR RANGO DE FECHA */
	@Query(value = "select f.idfactura as idfactura ,u.nomusu as nomusu, f.razoneliminacion as razoneliminacion, m.descripcion as modulo ,sum(rf.valorunitario * rf.cantidad) as total from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join modulos m on f.idmodulo = m.idmodulo join usuarios u on f.usuarioeliminacion = u.idusuario where f.fechaeliminacion between ?1 and ?2 group by f.idfactura, m.idmodulo, u.idusuario ", nativeQuery = true)
	public List<RepFacEliminadas> findEliminadasXfecha(LocalDate d, LocalDate h);

	/* REPORTE DE FACTURAS ANULADAS POR RANGO DE FECHA */
	@Query(value = "select f.idfactura as idfactura ,u.nomusu as nomusu, f.razonanulacion  as razoneliminacion, m.descripcion as modulo ,sum(rf.valorunitario * rf.cantidad) as total from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join modulos m on f.idmodulo = m.idmodulo join usuarios u on f.usuarioanulacion = u.idusuario where f.fechaanulacion between ?1 and ?2 group by f.idfactura, m.idmodulo, u.idusuario ", nativeQuery = true)
	public List<RepFacEliminadas> findAnuladasXfecha(LocalDate d, LocalDate h);

	// @Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad *
	// rf.valorunitario) as suma,f.feccrea from facturas f join rubroxfac rf on
	// f.idfactura = rf.idfactura_facturas where f.idfactura = ?1 and not (
	// rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5) group by
	// rf.idfactura_facturas , f.feccrea ", nativeQuery = true)
	@Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma, f.feccrea, f.formapago, f.fechatransferencia from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas  where f.idfactura = ?1 and not ( rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5 ) group by rf.idfactura_facturas , f.feccrea, f.formapago, f.fechatransferencia ", nativeQuery = true)
	public List<FacIntereses> getForIntereses(Long idfactura);

	/*
	 * REPORTE DE TRANSFERENCIAS
	 */
	// TRANSFERENCIAS REALIZADAS POR RANGO
	@Query(value = "select f.idfactura,f.nrofactura, sum(rf.cantidad * rf.valorunitario ) as total, f.fechatransferencia, f.idmodulo, c.nombre from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join abonados a on f.idabonado = a.idabonado join clientes c on a.idresponsable = c.idcliente where f.fechatransferencia between ?1 and ?2 and f.formapago = 4 and f.fechaeliminacion is null and f.fechaconvenio is null group by f.idfactura, f.nrofactura, c.nombre", nativeQuery = true)
	public List<FacTransferencias> getFacAllTransferidas(LocalDate d, LocalDate h);

	@Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario ) as total, f.fechatransferencia, f.idmodulo, c.nombre from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join abonados a on f.idabonado = a.idabonado join clientes c on a.idresponsable = c.idcliente where f.fechatransferencia between ?1 and ?2 and f.formapago = 4 and f.pagado = 1 and f.estado = 1 and f.fechaeliminacion is null and f.fechaconvenio is null group by f.idfactura, c.nombre", nativeQuery = true)
	public List<FacTransferencias> getFacPagadasTransferidas(LocalDate d, LocalDate h);

	@Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario ) as total, f.fechatransferencia, f.idmodulo, c.nombre from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join abonados a on f.idabonado = a.idabonado join clientes c on a.idresponsable = c.idcliente where f.fechatransferencia between ?1 and ?2 and f.formapago = 4 and f.pagado = 1 and f.estado = 3 and f.fechaeliminacion is null and f.fechaconvenio is null group by f.idfactura, c.nombre", nativeQuery = true)
	public List<FacTransferencias> getFacNoPagadasTransferidas(LocalDate d, LocalDate h);

	/* CARTERA VENCIDA POR FACTURAS */
	@Query(value = "SELECT" +
			"    rf.idfactura_facturas as factura," +
			"    c.nombre," +
			"    m.descripcion as modulo," +
			"    SUM(rf.cantidad * rf.valorunitario) AS total," +
			"    l.idabonado_abonados as cuenta," +
			"    e.emision ," +
			"    l.lecturaactual - l.lecturaanterior as m3" +
			" FROM" +
			" rubroxfac rf" +
			" JOIN facturas f ON rf.idfactura_facturas = f.idfactura" +
			" JOIN rubros r ON rf.idrubro_rubros = r.idrubro" +
			" JOIN clientes c ON f.idcliente = c.idcliente" +
			" JOIN modulos m ON f.idmodulo = m.idmodulo" +
			" JOIN lecturas l ON f.idfactura = l.idfactura " +
			" join emisiones e on l.idemision = e.idemision " +
			" WHERE" +
			"    f.totaltarifa > 0 and f.feccrea <= ?1 " +
			"    AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro > ?1 OR f.fechacobro IS NULL ) or f.estado = 3)"
			+
			"    AND f.fechaconvenio IS NULL" +
			"    AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)" +
			" GROUP BY" +
			"     rf.idfactura_facturas, c.nombre, m.descripcion, l.idlectura, l.idabonado_abonados , e.emision" +
			" ORDER BY" +
			" total DESC;", nativeQuery = true)
	public List<CarteraVencidaFacturas> getCVByFacturasConsumo(LocalDate fecha);

	@Query(value = "SELECT" +
			"    rf.idfactura_facturas as factura," +
			"    c.nombre, c.idcliente," +
			"    m.descripcion as modulo," +
			"    SUM(rf.cantidad * rf.valorunitario) AS total," +
			"    l.idabonado_abonados as cuenta," +
			"    e.emision," +
			"    l.lecturaactual - l.lecturaanterior as m3" +
			" FROM rubroxfac rf" +
			" JOIN facturas f ON rf.idfactura_facturas = f.idfactura" +
			" JOIN rubros r ON rf.idrubro_rubros = r.idrubro" +
			" JOIN clientes c ON f.idcliente = c.idcliente" +
			" JOIN modulos m ON f.idmodulo = m.idmodulo" +
			" JOIN lecturas l ON f.idfactura = l.idfactura" +
			" JOIN emisiones e ON l.idemision = e.idemision" +
			" WHERE f.totaltarifa > 0 AND f.feccrea <= ?1" +
			" AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro > ?1 OR f.fechacobro IS NULL) OR f.estado = 3)" +
			" AND f.fechaconvenio IS NULL" +
			" AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)" +
			" GROUP BY rf.idfactura_facturas, c.nombre, c.idcliente, m.descripcion, l.idlectura, l.idabonado_abonados, e.emision ORDER BY total DESC", countQuery = "SELECT COUNT(DISTINCT rf.idfactura_facturas) "
					+
					"FROM rubroxfac rf " +
					"JOIN facturas f ON rf.idfactura_facturas = f.idfactura " +
					"JOIN lecturas l ON f.idfactura = l.idfactura " +
					"WHERE f.totaltarifa > 0 AND f.feccrea <= ?1 " +
					"AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro >= ?1 OR f.fechacobro IS NULL) OR f.estado = 3) "
					+
					"AND f.fechaconvenio IS NULL " +
					"AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)",

			nativeQuery = true)
	Page<CarteraVencidaFacturas> getCVByConsumo(LocalDate fecha, Pageable pageable);

	@Query(value = "SELECT" + //
			"    rf.idfactura_facturas as factura," + //
			"    c.nombre," + //
			"    m.descripcion as modulo," + //
			"    SUM(rf.cantidad * rf.valorunitario) AS total, " + //
			"    f.idabonado as cuenta " + //
			" FROM" + //
			"    rubroxfac rf" + //
			" JOIN facturas f ON rf.idfactura_facturas = f.idfactura" + //
			" JOIN rubros r ON rf.idrubro_rubros = r.idrubro" + //
			" JOIN clientes c ON f.idcliente = c.idcliente" + //
			" JOIN modulos m ON f.idmodulo = m.idmodulo" + //
			" WHERE" + //
			"    f.totaltarifa > 0 and f.feccrea <= ?1 " + //
			"    AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro > ?1 OR f.fechacobro IS NULL ) or f.estado = 3)"
			+
			"    and not ( (f.idmodulo = 3 and f.idabonado > 0) or f.idmodulo = 4 )" + //
			"    AND f.fechaconvenio IS NULL" + //
			"    AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)" + //
			" GROUP BY" + //
			"     rf.idfactura_facturas, c.nombre, m.descripcion, f.idabonado  " + //
			" ORDER BY" + //
			" total desc;", nativeQuery = true)
	public List<CVFacturasNoConsumo> getCVByFacturasNoConsumo(LocalDate fecha);

	@Query(value = "SELECT" + //
			"    rf.idfactura_facturas as factura," + //
			"    c.nombre, c.idcliente," + //
			"    m.descripcion as modulo," + //
			"    SUM(rf.cantidad * rf.valorunitario) AS total, " + //
			"    f.idabonado as cuenta " + //
			" FROM" + //
			"    rubroxfac rf" + //
			" JOIN facturas f ON rf.idfactura_facturas = f.idfactura" + //
			" JOIN rubros r ON rf.idrubro_rubros = r.idrubro" + //
			" JOIN clientes c ON f.idcliente = c.idcliente" + //
			" JOIN modulos m ON f.idmodulo = m.idmodulo" + //
			" WHERE" + //
			"    f.totaltarifa > 0 and f.feccrea < ?1 " + //
			"    AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro >= ?1 OR f.fechacobro IS NULL ) or f.estado = 3)"
			+
			"    and not ( (f.idmodulo = 3 and f.idabonado > 0) or f.idmodulo = 4 )" + //
			"    AND f.fechaconvenio IS NULL" + //
			"    AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)" + //
			" GROUP BY" + //
			"     rf.idfactura_facturas, c.nombre, c.idcliente, m.descripcion, f.idabonado  " + //
			" ORDER BY" + //
			" total DESC", countQuery = "SELECT COUNT(DISTINCT rf.idfactura_facturas) " + //
					"FROM rubroxfac rf " + //
					"JOIN facturas f ON rf.idfactura_facturas = f.idfactura " + //
					"WHERE f.totaltarifa > 0 and f.feccrea <= ?1 " + //
					"AND ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro >= ?1 OR f.fechacobro IS NULL ) or f.estado = 3) "
					+ //
					"AND not ( (f.idmodulo = 3 and f.idabonado > 0) or f.idmodulo = 4 ) " + //
					"AND f.fechaconvenio IS NULL " + //
					"AND f.fechaeliminacion IS NULL AND rf.idrubro_rubros NOT IN (79, 5, 165)", nativeQuery = true)
	public Page<CVFacturasNoConsumo> getCVByNoConsumo(LocalDate fecha, Pageable pageable);

	/* CONSULTA PARA LAS REMISIONES DE MULTAS HE INTERESES */
	@Query(value = "SELECT f.idfactura, m.descripcion, f.feccrea, SUM(rf.valorunitario * rf.cantidad) AS total " +
			"FROM rubroxfac rf " +
			"JOIN facturas f ON rf.idfactura_facturas = f.idfactura " +
			"JOIN modulos m ON f.idmodulo = m.idmodulo " +
			"JOIN clientes c ON f.idcliente = c.idcliente " +
			"WHERE f.totaltarifa > 0 " +
			"AND f.idcliente = ?1 " +
			"AND ((f.estado IN (1, 2) AND f.fechacobro IS NULL) OR f.estado = 3) " +
			"AND f.fechaeliminacion IS NULL " +
			"AND f.fechaconvenio IS NULL " +
			"AND rf.idrubro_rubros NOT IN (165, 5, 6) " +
			"AND m.idmodulo IN (3, 4, 27) " +
			"AND f.feccrea <= ?2 " +
			"GROUP BY f.idfactura, m.descripcion, f.feccrea " +
			"ORDER BY m.descripcion ASC", nativeQuery = true)
	public List<Remision> getFacForRemisiones(Long idcliente, LocalDate topefecha);

	@Query(value = "SELECT f.idfactura,f.nrofactura, m.descripcion, f.feccrea, SUM(rf.valorunitario * rf.cantidad) AS total "
			+
			"FROM rubroxfac rf " +
			"JOIN facturas f ON rf.idfactura_facturas = f.idfactura " +
			"JOIN modulos m ON f.idmodulo = m.idmodulo " +
			"JOIN clientes c ON f.idcliente = c.idcliente " +
			"WHERE f.totaltarifa > 0 " +
			"AND f.idcliente = ?1 " +
			"AND f.idabonado = ?2 " +
			"AND ((f.estado IN (1, 2) AND f.fechacobro IS NULL) OR f.estado = 3) " +
			"AND f.fechaeliminacion IS NULL " +
			"AND f.fechaconvenio IS NULL " +
			"AND rf.idrubro_rubros NOT IN (165, 5, 6) " +
			"AND m.idmodulo IN (3, 4, 27) " +
			"AND f.feccrea <= ?3 " +
			"GROUP BY f.idfactura, m.descripcion, f.feccrea, f.nrofactura " +
			"ORDER BY m.descripcion ASC", nativeQuery = true)
	public List<Remision> getFacForRemisionesAbonados(Long idcliente, Long cuenta, LocalDate topefecha);

	/*
	 * 
	 * 
	 * 
	 * SELECT r.fechacobro, f.idfactura, sum(rf.cantidad * rf.valorunitario)
	 * FROM facxrecauda fr
	 * JOIN facturas f ON fr.idfactura = f.idfactura
	 * JOIN recaudacion r ON fr.idrecaudacion = r.idrecaudacion
	 * join rubroxfac rf on fr.idfactura = rf.idfactura_facturas
	 * WHERE f.usuariocobro = 22
	 * AND f.fechacobro = '2024-09-30'
	 * AND CAST(r.fechacobro AS TIME) between'07:30:00' and '11:00:00'
	 * group by f.idfactura, r.fechacobro ;
	 * 
	 * 
	 */

	/*
	 * @Query(value =
	 * "select f.idfactura as factura, c.nombre , sum(rf.cantidad * rf.valorunitario) as total from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura join rubros r on rf.idrubro_rubros = r.idrubro join clientes c on f.idcliente = c.idcliente "
	 * +
	 * "where r.idrubro = ?1 and f.totaltarifa > 0 and f.feccrea <= ?2 and (( (f.estado = 1 or f.estado = 2) and ( f.fechacobro >= ?2 or f.fechacobro is null)) or f.estado = 3 ) "
	 * +
	 * "and f.fechaconvenio is null and f.fechaeliminacion is null group by f.idfactura, c.nombre 	"
	 * , nativeQuery = true)
	 */
	@Query(value = """
						SELECT
			    f.idfactura AS factura,
			    c.nombre AS nombre,
			    SUM(rf.cantidad * rf.valorunitario) AS total,
			    tf.totalFactura
			FROM
			    facturas f
			JOIN clientes c ON f.idcliente = c.idcliente
			JOIN rubroxfac rf ON rf.idfactura_facturas = f.idfactura
			JOIN rubros r ON rf.idrubro_rubros = r.idrubro
			JOIN (
			    SELECT rf2.idfactura_facturas, SUM(rf2.cantidad * rf2.valorunitario) AS totalFactura
			    FROM rubroxfac rf2
			    GROUP BY rf2.idfactura_facturas
			) tf ON tf.idfactura_facturas = f.idfactura
			WHERE
			    r.idrubro = ?1
			    AND f.totaltarifa > 0
			    AND f.feccrea <= ?2
			    AND (
			        ((f.estado = 1 OR f.estado = 2) AND (f.fechacobro >= ?2 OR f.fechacobro IS NULL))
			        OR f.estado = 3
			    )
			    AND f.fechaconvenio IS NULL
			    AND f.fechaeliminacion IS NULL
				AND rf.idrubro_rubros NOT IN (79, 5, 165)
			GROUP BY
			    f.idfactura, c.nombre, tf.totalFactura
			ORDER BY total DESC
						""", nativeQuery = true)
	public List<CVFacturasNoConsumo> getCvFacturasByRubro(Long idrubro, LocalDate fecha);

	@Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario) as subtotal from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas where f.idabonado = ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is null group by f.idfactura ORDER BY f.idfactura", nativeQuery = true)
	public List<FacturasSinCobroInter> findFacturasSinCobro(Long cuenta);

	@Query(value = "select f.idfactura, sum(rf.cantidad * rf.valorunitario) as subtotal, c.nombre, c.cedula, a.idabonado as cuenta, a.direccionubicacion, f.formapago, e.feccrea, f.fechatransferencia as fectransferencia "
			+
			"from facturas f join rubroxfac rf on f.idfactura = rf.idfactura_facturas " +
			"join clientes c on c.idcliente = f.idcliente " +
			"join lecturas l on l.idfactura = f.idfactura " +
			"join emisiones e on l.idemision = e.idemision " +
			"join abonados a on a.idabonado = f.idabonado " +
			"where f.idabonado = ?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is null "
			+
			"group by f.idfactura, c.nombre, c.cedula, a.idabonado, a.direccionubicacion, e.feccrea ORDER BY f.idfactura", nativeQuery = true)
	public List<FacturasSinCobroInter> findSincobroDatos(Long cuenta);

	/*
	 * obtener todas las facturas que estan sin cobrar para calcularles el interes
	 * mensualmente y guardarlas en tmpintresxfac
	 */
	@Query(value = """
			select
				f.idfactura
			from
				facturas f
			where
				f.totaltarifa > 0
				and (
				(f.estado = 1
					or f.estado = 2)
				and (f.fechacobro is null)
					or f.estado = 3)
				and f.fechaconvenio is null
				and f.fechaeliminacion is null
			""", nativeQuery = true)
	public List<FacSinCobrar> getIdsFromFacturasSincobrar();

	/*
	 * 
	 * select
	 * a.idabonado ,
	 * sum(rf.cantidad * rf.valorunitario),
	 * count(f) as facturas
	 * from
	 * facturas f
	 * join rubroxfac rf on
	 * f.idfactura = rf.idfactura_facturas
	 * join abonados a on
	 * a.idabonado = f.idabonado
	 * join rutas r on r.idruta = a.idruta_rutas
	 * where a.idruta_rutas =2 and
	 * f.idabonado > 0 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is
	 * null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaeliminacion is
	 * null
	 * group by
	 * a.idabonado
	 */

}
