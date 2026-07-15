package com.epmapat.erp_epmapat.repositorio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.Async;

import com.epmapat.erp_epmapat.interfaces.ConsumoxCat_int;
import com.epmapat.erp_epmapat.interfaces.CierreRutaCategoria;
import com.epmapat.erp_epmapat.interfaces.CierreRutaMultaDetalle;
import com.epmapat.erp_epmapat.interfaces.CierreRutaResumenTotales;
import com.epmapat.erp_epmapat.interfaces.CierreRutaRubroResumen;
import com.epmapat.erp_epmapat.interfaces.CountRubrosByEmision;
import com.epmapat.erp_epmapat.interfaces.ControlRutaStats;
import com.epmapat.erp_epmapat.interfaces.EmisionesInterface;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.interfaces.FacturaCuentaView;
import com.epmapat.erp_epmapat.interfaces.FecEmision;
import com.epmapat.erp_epmapat.interfaces.RepEmisionEmi;
import com.epmapat.erp_epmapat.interfaces.RepFacEliminadasByEmision;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Lecturas;

public interface LecturasR extends JpaRepository<Lecturas, Long> {

	@EntityGraph(attributePaths = {
			"idrutaxemision_rutasxemision",
			"idnovedad_novedades",
			"idabonado_abonados",
			"idabonado_abonados.idresponsable",
			"idabonado_abonados.idcliente_clientes",
			"idabonado_abonados.idcategoria_categorias",
			"idabonado_abonados.idruta_rutas"
	})
	@Override
	Optional<Lecturas> findById(Long id);

	// Lectura por Planilla (Es una a una)
	@EntityGraph(attributePaths = {
			"idrutaxemision_rutasxemision",
			"idrutaxemision_rutasxemision.idemision_emisiones",
			"idrutaxemision_rutasxemision.idruta_rutas",
			"idnovedad_novedades",
			"idabonado_abonados",
			"idabonado_abonados.idresponsable",
			"idabonado_abonados.idcliente_clientes",
			"idabonado_abonados.idcategoria_categorias",
			"idabonado_abonados.idruta_rutas"
	})
	@Query("SELECT l FROM Lecturas l WHERE l.idfactura = ?1")
	public Lecturas findOnefactura(Long idfactura);

	// Lecturas por rutasxemision
	@EntityGraph(attributePaths = {
			"idrutaxemision_rutasxemision",
			"idrutaxemision_rutasxemision.idemision_emisiones",
			"idrutaxemision_rutasxemision.idruta_rutas",
			"idnovedad_novedades",
			"idabonado_abonados",
			"idabonado_abonados.idresponsable",
			"idabonado_abonados.idcliente_clientes",
			"idabonado_abonados.idcategoria_categorias",
			"idabonado_abonados.idruta_rutas"
	})
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idrutaxemision_rutasxemision.idrutaxemision = ?1
			ORDER BY a.idabonado
			""")
	public List<Lecturas> findByIdrutaxemision(Long idrutasxemision);

	// Lecturas por Abonado (Historial de consumo)
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE a.idabonado = ?1
			ORDER BY l.idlectura DESC
			""")
	public List<Lecturas> findByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM lecturas WHERE mesesmulta>=4 and estado=1 LIMIT 20", nativeQuery = true)
	public List<Lecturas> findByMonth();

	@Query(value = "SELECT * FROM lecturas WHERE idabonado_abonados=?1 ", nativeQuery = true)
	public List<Lecturas> findLecturasByIdAbonados(Long idabonado);

	@EntityGraph(attributePaths = {
			"idrutaxemision_rutasxemision",
			"idrutaxemision_rutasxemision.idemision_emisiones",
			"idrutaxemision_rutasxemision.idruta_rutas",
			"idabonado_abonados",
			"idabonado_abonados.idresponsable",
			"idabonado_abonados.idcliente_clientes",
			"idabonado_abonados.idcategoria_categorias",
			"idabonado_abonados.idruta_rutas"
	})
	@Query("SELECT l FROM Lecturas l WHERE l.idrutaxemision_rutasxemision.idrutaxemision = ?1")
	public List<Lecturas> findByIdRutasxEmision(Long idrutaxemision);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN rutasxemision r ON l.idrutaxemision_rutasxemision = r.idrutaxemision INNER JOIN rutas r2 ON r.idruta_rutas = ?1", nativeQuery = true)
	public List<Lecturas> findByRutas(Long idrutas);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado  WHERE l.idabonado_abonados = ?1 AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByIdAbonado(Long idabonado);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado INNER JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE LOWER(c.nombre) LIKE %?1% AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByNCliente(String nombre);

	@Query(value = "SELECT * FROM lecturas l INNER JOIN abonados a ON l.idabonado_abonados = a.idabonado INNER JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE c.cedula LIKE %?1% AND mesesmulta >=4", nativeQuery = true)
	public List<Lecturas> findByICliente(String identificacion);

	// Lectura por Planilla (Es una a una)
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idfactura = ?1
			ORDER BY l.idlectura DESC
			""")
	public List<Lecturas> findByIdfactura(Long idfactura);

	// Lecturas de una Emisión
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idemision = ?1
			ORDER BY a.idabonado, l.idlectura DESC
			""")
	public List<Lecturas> findByIdemision(Long idemision);

	@EntityGraph(attributePaths = {
			"idrutaxemision_rutasxemision",
			"idrutaxemision_rutasxemision.idemision_emisiones",
			"idrutaxemision_rutasxemision.idruta_rutas",
			"idnovedad_novedades",
			"idabonado_abonados",
			"idabonado_abonados.idresponsable",
			"idabonado_abonados.idcliente_clientes",
			"idabonado_abonados.idcategoria_categorias",
			"idabonado_abonados.idruta_rutas"
	})
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idemision = ?1
			  AND l.idfactura IS NULL
			ORDER BY a.idabonado, l.idlectura DESC
			""")
	public List<Lecturas> findByIdemisionAndIdfacturaIsNull(Long idemision);

	// Lecturas de una Emisión
	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idemision = ?1
			  AND a.idabonado = ?2
			ORDER BY l.idlectura DESC
			""")
	public List<Lecturas> findByIdemisionIdAbonado(Long idemision, Long idabonado);

	@Query("""
			SELECT l
			FROM Lecturas l
			LEFT JOIN FETCH l.idrutaxemision_rutasxemision re
			LEFT JOIN FETCH re.idemision_emisiones
			LEFT JOIN FETCH re.idruta_rutas
			LEFT JOIN FETCH l.idnovedad_novedades
			LEFT JOIN FETCH l.idabonado_abonados a
			LEFT JOIN FETCH a.idresponsable
			LEFT JOIN FETCH a.idcliente_clientes
			LEFT JOIN FETCH a.idcategoria_categorias
			LEFT JOIN FETCH a.idruta_rutas
			WHERE l.idemision = ?1
			  AND a.idabonado = ?2
			ORDER BY l.idlectura DESC
			""")
	Optional<Lecturas> findFirstByIdemisionAndIdabonado(Long idemision, Long idabonado);

	@Query(value = """
			SELECT COUNT(DISTINCT l.idabonado_abonados)
			FROM lecturas l
			WHERE l.idrutaxemision_rutasxemision = ?1
			""", nativeQuery = true)
	Long countDistinctAbonadosByRutaXEmision(Long idrutaxemision);

	// Ultima lectura de un Abonado: debe ser lecturaactual tempoaralmente
	// lecturaanterior porque no están cerradas las rutas de la emisión anterior
	@Query(value = "SELECT l.lecturaactual FROM lecturas l WHERE l.idabonado_abonados=?1 ORDER BY l.idemision DESC LIMIT 1", nativeQuery = true)
	public Long ultimaLectura(Long idabonado);

	@Query(value = "SELECT l.lecturaactual FROM lecturas l WHERE l.idabonado_abonados=?1 and l.idemision =?2 ORDER BY l.idemision DESC LIMIT 1", nativeQuery = true)
	public Long ultimaLecturaByIdemision(Long idabonado, long idemision);

	@Query(value = "select sum(f.totaltarifa) from lecturas l join facturas f on l.idfactura = f.idfactura where l.idemision = ?1", nativeQuery = true)
	public BigDecimal totalEmisionXFactura(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) join rubros r on rf.idrubro_rubros = r.idrubro where l.idemision = ?1 group by r.idrubro", nativeQuery = true)
	public List<Object[]> RubrosEmitidos(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) join rubros r on rf.idrubro_rubros = r.idrubro where not f.fechaeliminacion is null and not f.usuarioeliminacion is null and  l.idemision = ?1 group by r.idrubro", nativeQuery = true)
	public List<Object[]> R_EmisionFinal(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) join rubros r on rf.idrubro_rubros = r.idrubro where f.fechaeliminacion  is null and f.usuarioeliminacion is null and  l.idemision = ?1 group by r.idrubro;", nativeQuery = true)
	public List<Object[]> R_EmisionActual(Long idemision);

	/* REPORTE DEUDORES */
	@Query(value = "select * from lecturas l join facturas f on l.idfactura = f.idfactura join rutasxemision re on l.idrutaxemision_rutasxemision = re.idrutaxemision join rutas r on re.idruta_rutas = r.idruta  where f.pagado = 0 and f.fechaeliminacion is null and r.idruta = ?1 and f.estadoconvenio = 0", nativeQuery = true)
	public List<Lecturas> findDeudoresByRuta(Long ruta);

	/* encontrar fecha de emision para recaudacion */
	@Query(value = """
			select max(e.feccrea)
			from lecturas l
			join emisiones e on l.idemision = e.idemision
			where l.idfactura = ?1
			""", nativeQuery = true)
	public Date findDateByIdfactura(Long idfactura);

	@Query(value = "select e.emision, e.feccrea from lecturas l join emisiones e on l.idemision = e.idemision where l.idfactura =  ?1", nativeQuery = true)
	public List<FecEmision> getEmisionByIdfactura(Long idfactura);

	@Query(value = "SELECT * FROM emisiones e join lecturas l on e.idemision = l.idemision join facturas f on l.idfactura = f.idfactura where not f.fechaeliminacion is null and l.idemision = ?1 order by f.idabonado", nativeQuery = true)
	List<Lecturas> findByIdEmisiones(Long idemision);

	@Query(value = "select\r\n" + //
			"\trf.idfactura_facturas as planilla,\r\n" + //
			"\tl.idlectura,\r\n" + //
			"\te.emision ,\r\n" + //
			"\tl.idabonado_abonados as cuenta,\r\n" + //
			"\tc.nombre,\r\n" + //
			"\tr.descripcion as ruta, \r\n" + //
			"\tsum(rf.cantidad * rf.valorunitario) as suma\r\n" + //
			"from\r\n" + //
			"\temisiones e\r\n" + //
			"join lecturas l on\r\n" + //
			"\te.idemision = l.idemision\r\n" + //
			"join facturas f on\r\n" + //
			"\tl.idfactura = f.idfactura\r\n" + //
			"join clientes c on\r\n" + //
			"\tf.idcliente = c.idcliente\r\n" + //
			"join abonados a on\r\n" + //
			"\tl.idabonado_abonados = a.idabonado\r\n" + //
			"join rutas r on\r\n" + //
			"\ta.idruta_rutas = r.idruta\r\n" + //
			"join rubroxfac rf on \r\n" + //
			"\tl.idfactura = rf.idfactura_facturas\r\n" + //
			"where\r\n" + //
			"\tnot f.fechaeliminacion is null\r\n" + //
			"\tand l.idemision = ?1\r\n" + //
			"group by\r\n" + //
			"\trf.idfactura_facturas,\r\n" + //
			"\tl.idlectura,\r\n" + //
			"\te.emision ,\r\n" + //
			"\tl.idabonado_abonados,\r\n" + //
			"\tc.nombre,\r\n" + //
			"\tr.descripcion\r\n" + //
			"order by\r\n" + //
			"\tl.idabonado_abonados", nativeQuery = true)
	List<RepFacEliminadasByEmision> findByIdEmisionesR(Long idemision);

	/* REPORTES DE LOS RUBROS DE LA EMISION INICIAL */
	@Async
	@Query(value = " WITH max_fechaemision AS ( "
			+ " SELECT l.fechaemision "
			+ " FROM lecturas l "
			+ " WHERE l.idemision = ?1 "
			+ " GROUP BY l.fechaemision "
			+ " ORDER BY COUNT(*) desc "
			+ " LIMIT 1) "
			+ "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(a.idabonado) as abonados "
			+ "FROM lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) join rubros r on rf.idrubro_rubros = r.idrubro "
			+ "join abonados a on l.idabonado_abonados = a.idabonado "
			+ "WHERE l.idemision = ?1 and not rf.idrubro_rubros = 5 "
			+ "AND l.fechaemision = (SELECT fechaemision FROM max_fechaemision) "
			+ "group by rf.idrubro_rubros , r.descripcion ; ", nativeQuery = true)
	CompletableFuture<List<RubroxfacIReport>> getAllRubrosEmisionInicial(Long idemision);

	@Async
	@Query(value = "WITH max_fechaemision AS ( " +
			"  SELECT l.fechaemision " +
			"  FROM lecturas l " +
			"  WHERE l.idemision = ?1" +
			"  GROUP BY l.fechaemision " +
			"  ORDER BY COUNT(*) desc " +
			"  LIMIT 1) " +
			" select  count(a.idabonado) as abonados, sum(l.lecturaactual - l.lecturaanterior) as m3" +
			" FROM lecturas l " +
			" join abonados a on l.idabonado_abonados = a.idabonado " +
			" WHERE l.idemision = ?1 " +
			" AND l.fechaemision = (SELECT fechaemision FROM max_fechaemision) ", nativeQuery = true)
	CompletableFuture<List<RubroxfacIReport>> getCuentaM3AllEmiInicial(Long idemision);

	/*--REPORTE DE LOS RUBROS nuevos */
	@Async
	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total, count(l.idabonado_abonados) as abonados "
			+ "from emisionindividual ei "
			+ "join lecturas l on ei.idlecturanueva = l.idlectura  "
			+ "join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) "
			+ "join facturas f on rf.idfactura_facturas = f.idfactura "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro  "
			+ "where ei.idemision = ?1 and f.fechaeliminacion is null and not rf.idrubro_rubros = 5 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllNewLecturas(Long idemision);

	/*
	 * --REPORTE DE LOS RUBROS ELIMINADOS
	 */
	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(l.idabonado_abonados) as abonados "
			+ "from lecturas l  "
			+ "join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro  "
			+ "where l.idemision = ?1 and not rf.idrubro_rubros = 5 and not l.observaciones is null "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllDeleteLecturas(Long idemision);

	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(l.idabonado_abonados) as abonados "
			+ "from emisionindividual ei "
			+ "join lecturas l on ei.idlecturaanterior = l.idlectura "
			+ "join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro  "
			+ "where ei.idemision = ?1 and not rf.idrubro_rubros = 5 and rf.valorunitario > 0 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> _getAllDeleteLecturas(Long idemision);

	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(l.idabonado_abonados) as abonados "
			+ "from lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) "
			+ "join facturas f on rf.idfactura_facturas = f.idfactura "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro "
			+ "where l.idemision = ?1 and f.fechaeliminacion is null and not rf.idrubro_rubros = 5 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllActual(Long idemision);

	@Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma, e.feccrea, f.formapago, f.fechatransferencia from lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas and (rf.estado is null or rf.estado <> 0) join emisiones e on l.idemision = e.idemision join facturas f on l.idfactura = f.idfactura where l.idfactura = ?1 and not (rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5 ) group by rf.idfactura_facturas, e.feccrea, f.formapago, f.fechatransferencia", nativeQuery = true)
	public List<FacIntereses> getForIntereses(Long idfactura);

	@Query(value = "select cl.nombre, a.idabonado as cuenta, sum(rf.cantidad * rf.valorunitario) as valEmitido, c.descripcion as categoria, l.lecturaactual - l.lecturaanterior as m3 from lecturas l join clientes cl on l.idresponsable = cl.idcliente join abonados a on l.idabonado_abonados = a.idabonado join rubroxfac rf on rf.idfactura_facturas = l.idfactura and (rf.estado is null or rf.estado <> 0) join categorias c on l.idcategoria = c.idcategoria where idemision = ?1 and not rf.idrubro_rubros = 6 and l.observaciones is null group by a.idabonado, c.descripcion, cl.idcliente, l.lecturaanterior, l.lecturaactual order by a.idabonado asc", nativeQuery = true)
	public List<RepEmisionEmi> getReporteValEmitidosxEmision(Long idemision);

	// REPORTE DE EMISIONES X CATEGORIA
	@Query(value = "select l.idcategoria,c.descripcion, count(l.idabonado_abonados) as cuentas, sum(l.lecturaactual-l.lecturaanterior) as m3, sum(f.totaltarifa) as total "
			+ "from lecturas l "
			+ "join abonados a on l.idabonado_abonados = a.idabonado "
			+ "join categorias c on l.idcategoria = c.idcategoria "
			+ "join facturas f on l.idfactura = f.idfactura "
			+ "where l.idemision = ?1 and l.observaciones is null "
			+ "group by l.idcategoria, c.descripcion ", nativeQuery = true)
	public List<ConsumoxCat_int> getConsumoxCategoria(Long idemision);

	@Query(value = "WITH abonados_con_emision AS ( SELECT DISTINCT " +
			" l.idabonado_abonados FROM lecturas l " +
			" WHERE l.idemision=?1) " +
			" SELECT a.idabonado_abonados,l.idfactura, l.lecturaanterior, l.lecturaactual, COUNT(rf.idrubro_rubros) AS rubros_count"
			+
			" FROM abonados_con_emision a" +
			" LEFT JOIN lecturas l" +
			" ON a.idabonado_abonados = l.idabonado_abonados AND l.idemision = ?1" +
			" LEFT JOIN rubroxfac rf" +
			" ON rf.idfactura_facturas = l.idfactura" +
			" GROUP BY a.idabonado_abonados,l.idfactura, l.lecturaanterior, l.lecturaactual " +
			"HAVING COUNT(rf.idrubro_rubros) = 0 ", nativeQuery = true)
	public List<CountRubrosByEmision> getCuentaRubrosByEmision(long idemision);

	@Query(value = """
					SELECT
			    l.idfactura,
			    SUM(l.lecturaactual - l.lecturaanterior) AS m3,
			    a.idabonado as cuenta,
			    l.idcategoria as categoria,
			    a.swalcantarillado as swAguapotable ,
			    a.municipio as swMunicipio ,
			    a.adultomayor  as swAdultoMayor
			FROM lecturas l
			JOIN abonados a
			    ON l.idabonado_abonados = a.idabonado
			WHERE l.idemision = ?1
			  AND a.swalcantarillado = TRUE
			GROUP BY l.idfactura, a.idabonado, l.idcategoria
			ORDER BY l.idfactura;
					""", nativeQuery = true)
	public List<EmisionesInterface> getSWalcatarillados(Long idemision);

	@Query(value = """
			SELECT
			    l.idfactura,
			    SUM(l.lecturaactual - l.lecturaanterior) AS m3,
			    a.idabonado as cuenta,
			    l.idcategoria AS categoria,
			    a.swalcantarillado AS swAguapotable,
			    a.municipio AS swMunicipio,
			    a.adultomayor AS swAdultoMayor
			FROM lecturas l
			JOIN abonados a ON l.idabonado_abonados = a.idabonado
			LEFT JOIN rubroxfac rf ON l.idfactura = rf.idfactura_facturas AND (rf.estado <> 0 OR rf.estado IS NULL)
			WHERE l.idemision = ?1
			GROUP BY
			    l.idfactura, a.idabonado, l.idcategoria,
			    a.swalcantarillado, a.municipio, a.adultomayor
			HAVING SUM(l.lecturaactual - l.lecturaanterior) >= 0
			   AND COUNT(rf.idrubro_rubros) = 0
			""", nativeQuery = true)
	List<EmisionesInterface> GetCuentasCeros(Long idemision);

	@Query(value = """
			SELECT
				COUNT(DISTINCT l.idabonado_abonados) AS cuentas,
				COALESCE(SUM(l.lecturaactual - l.lecturaanterior), 0) AS m3,
				COALESCE(SUM(f.totaltarifa), 0) AS total
			FROM lecturas l
			JOIN facturas f ON l.idfactura = f.idfactura
			WHERE l.idrutaxemision_rutasxemision = ?1
			  AND f.fechaeliminacion IS NULL
			  AND f.fechaanulacion IS NULL
			""", nativeQuery = true)
	public CierreRutaResumenTotales getResumenCierreRuta(Long idrutaxemision);

	@Query(value = """
			SELECT
				rx.idrutaxemision AS idrutaxemision,
				r.idruta AS idruta,
				r.codigo AS codigoRuta,
				r.descripcion AS nombreRuta,
				rx.estado AS estadoRuta,
				COUNT(l.idlectura) AS lecturas,
				COUNT(CASE WHEN l.idfactura IS NOT NULL THEN 1 END) AS lecturasConFactura,
				COUNT(CASE WHEN l.idfactura IS NULL THEN 1 END) AS lecturasSinFactura,
				COUNT(DISTINCT l.idabonado_abonados) AS abonados,
				COALESCE(SUM(l.lecturaactual - l.lecturaanterior), 0) AS m3,
				COALESCE(SUM(CASE
					WHEN f.fechaeliminacion IS NULL AND f.fechaanulacion IS NULL
					THEN f.totaltarifa
					ELSE 0
				END), 0) AS emitido,
				COALESCE(SUM(CASE
					WHEN f.fechaeliminacion IS NULL
					 AND f.fechaanulacion IS NULL
					 AND COALESCE(f.pagado, 0) <> 0
					THEN f.totaltarifa
					ELSE 0
				END), 0) AS cobrado,
				COALESCE(SUM(CASE
					WHEN f.fechaeliminacion IS NULL
					 AND f.fechaanulacion IS NULL
					 AND COALESCE(f.pagado, 0) = 0
					THEN f.totaltarifa
					ELSE 0
				END), 0) AS pendiente
			FROM rutasxemision rx
			JOIN rutas r ON rx.idruta_rutas = r.idruta
			LEFT JOIN lecturas l ON l.idrutaxemision_rutasxemision = rx.idrutaxemision
			LEFT JOIN facturas f ON l.idfactura = f.idfactura
			WHERE rx.idemision_emisiones = ?1
			GROUP BY rx.idrutaxemision, r.idruta, r.codigo, r.descripcion, rx.estado
			ORDER BY r.codigo
			""", nativeQuery = true)
	List<ControlRutaStats> getControlRutaStatsByEmision(Long idemision);

	@Query(value = """
			SELECT
				c.idcategoria AS idcategoria,
				c.descripcion AS descripcion,
				COUNT(DISTINCT l.idabonado_abonados) AS cuentas,
				COALESCE(SUM(f.totaltarifa), 0) AS total
			FROM lecturas l
			JOIN facturas f ON l.idfactura = f.idfactura
			JOIN categorias c ON l.idcategoria = c.idcategoria
			WHERE l.idrutaxemision_rutasxemision = ?1
			  AND f.fechaeliminacion IS NULL
			  AND f.fechaanulacion IS NULL
			GROUP BY c.idcategoria, c.descripcion
			ORDER BY c.descripcion
			""", nativeQuery = true)
	public List<CierreRutaCategoria> getCategoriasCierreRuta(Long idrutaxemision);

	@Query(value = """
			SELECT
				r.idrubro AS idrubro,
				r.descripcion AS descripcion,
				COALESCE(SUM(rf.cantidad * rf.valorunitario), 0) AS total,
				COUNT(DISTINCT l.idabonado_abonados) AS abonados
			FROM lecturas l
			JOIN facturas f ON l.idfactura = f.idfactura
			JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas AND (rf.estado IS NULL OR rf.estado <> 0)
			JOIN rubros r ON rf.idrubro_rubros = r.idrubro
			WHERE l.idrutaxemision_rutasxemision = ?1
			  AND f.fechaeliminacion IS NULL
			  AND f.fechaanulacion IS NULL
			GROUP BY r.idrubro, r.descripcion
			ORDER BY r.idrubro
			""", nativeQuery = true)
	public List<CierreRutaRubroResumen> getRubrosCierreRuta(Long idrutaxemision);

	@Query(value = """
			WITH cierre AS (
				SELECT rx.idrutaxemision, rx.fechacierre, rx.idemision_emisiones AS idemision
				FROM rutasxemision rx
				WHERE rx.idrutaxemision = ?1
			),
			cuentas_multa AS (
				SELECT DISTINCT l.idabonado_abonados AS cuenta, l.idfactura
				FROM lecturas l
				JOIN facturas f ON l.idfactura = f.idfactura
				JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas AND (rf.estado IS NULL OR rf.estado <> 0)
				CROSS JOIN cierre c
				WHERE l.idrutaxemision_rutasxemision = ?1
				  AND f.fechaeliminacion IS NULL
				  AND f.fechaanulacion IS NULL
				  AND f.feccrea <= c.fechacierre
				  AND COALESCE(f.estadoconvenio, 0) = 0
				  AND NOT EXISTS (
					  SELECT 1
					  FROM lecturas lx
					  WHERE lx.idfactura = f.idfactura
					    AND lx.idemision > c.idemision
				  )
				  AND (
					  COALESCE(f.pagado, 0) = 0
					  OR f.fechacobro IS NULL
					  OR f.fechacobro > c.fechacierre
				  )
				  AND rf.idrubro_rubros = 6
			),
			pendientes_al_cierre AS (
				SELECT
					f.idabonado,
					COUNT(*) AS pendientesalcierre,
					STRING_AGG(CAST(f.idfactura AS text), ', ' ORDER BY f.feccrea, f.idfactura) AS facturaspendientes
				FROM facturas f
				CROSS JOIN cierre c
				WHERE f.idabonado IS NOT NULL
				  AND f.feccrea <= c.fechacierre
				  AND f.fechaeliminacion IS NULL
				  AND f.fechaanulacion IS NULL
				  AND COALESCE(f.estadoconvenio, 0) = 0
				  AND NOT EXISTS (
					  SELECT 1
					  FROM lecturas lx
					  WHERE lx.idfactura = f.idfactura
					    AND lx.idemision > c.idemision
				  )
				  AND (
					  COALESCE(f.pagado, 0) = 0
					  OR f.fechacobro IS NULL
					  OR f.fechacobro > c.fechacierre
				  )
				GROUP BY f.idabonado
			)
			SELECT
				a.idabonado AS cuenta,
				cl.nombre AS nombre,
				COALESCE(resp.cedula, '') AS cedula,
				cat.descripcion AS categoria,
				f.idfactura AS idfactura,
				f.nrofactura AS nrofactura,
				COALESCE(SUM(rf.cantidad * rf.valorunitario), 0) AS multa,
				COALESCE(p.pendientesalcierre, 0) AS pendientesalcierre,
				COALESCE(p.facturaspendientes, '') AS facturaspendientes,
				COALESCE(f.totaltarifa, 0) AS totalfactura,
				f.fechacobro AS fechacobro
			FROM cuentas_multa cm
			JOIN facturas f ON cm.idfactura = f.idfactura
			JOIN abonados a ON cm.cuenta = a.idabonado
			JOIN clientes cl ON a.idcliente_clientes = cl.idcliente
			LEFT JOIN clientes resp ON a.idresponsable = resp.idcliente
			LEFT JOIN categorias cat ON a.idcategoria_categorias = cat.idcategoria
			JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas
				AND (rf.estado IS NULL OR rf.estado <> 0)
				AND rf.idrubro_rubros = 6
			LEFT JOIN pendientes_al_cierre p ON p.idabonado = a.idabonado
			GROUP BY
				a.idabonado,
				cl.nombre,
				resp.cedula,
				cat.descripcion,
				f.idfactura,
				f.nrofactura,
				p.pendientesalcierre,
				p.facturaspendientes,
				f.totaltarifa,
				f.fechacobro
			ORDER BY a.idabonado, f.idfactura
			""", nativeQuery = true)
	public List<CierreRutaMultaDetalle> getMultasCierreRuta(Long idrutaxemision);

	@Query(value = """
			  SELECT
			    l.idabonado_abonados AS cuenta,
			    f.idfactura,
			    MAX(l.lecturaactual - l.lecturaanterior)        AS m3,
			    l.idcategoria                                    AS categoria,
			    a.swalcantarillado                               AS swAguapotable,
			    a.municipio                                      AS swMunicipio,
			    a.adultomayor                                    AS swAdultomayor,
			    COUNT(DISTINCT rf.idrubroxfac)                   AS totalRubros
			  FROM lecturas l
			  JOIN facturas  f  ON l.idfactura = f.idfactura
			  JOIN rubroxfac rf ON f.idfactura = rf.idfactura_facturas AND (rf.estado <> 0 OR rf.estado IS NULL)
			  JOIN abonados  a  ON l.idabonado_abonados = a.idabonado
			  WHERE l.idemision = :idemision
			  GROUP BY
			    l.idabonado_abonados,
			    f.idfactura,
			    a.swalcantarillado,
			    a.municipio,
			    a.adultomayor,
			    l.idcategoria
			HAVING COUNT(DISTINCT rf.idrubroxfac) > :top
			ORDER BY l.idabonado_abonados DESC
			  """, nativeQuery = true)
	List<EmisionesInterface> getDuplicadosToRecalculate(
			@Param("idemision") Long idemision,
			@Param("top") Long top);

	// 1️⃣ Listar lecturas pendientes para preview
	@Query(value = """
			  SELECT l.*
			  FROM lecturas l
			  JOIN facturas f ON l.idfactura = f.idfactura
			  WHERE l.idresponsable = :idcliente
			    AND f.pagado = 0
			""", nativeQuery = true)
	List<Lecturas> findPendientesByCliente(@Param("idcliente") Long idcliente);

	@Query(value = """
			    SELECT DISTINCT l.idfactura AS idfactura, l.idabonado_abonados AS cuenta
			    FROM lecturas l
			    WHERE l.idemision = :idemision
			      AND l.idrutaxemision_rutasxemision = :idruta
			      AND l.idfactura IS NOT NULL
			""", nativeQuery = true)
	List<FacturaCuentaView> findFacturasByEmisionAndRuta(@Param("idemision") Long idemision,
			@Param("idruta") Long idruta);

	// 2️⃣ Reasignar lecturas al cliente master (MERGE)
	@Modifying
	@Transactional
	@Query(value = """
			  UPDATE lecturas
			  SET idresponsable = :masterId
			  WHERE idresponsable = :dupId
			    AND idfactura IN (
			        SELECT f.idfactura
			        FROM facturas f
			        WHERE f.pagado = 0
			    )
			""", nativeQuery = true)
	void reasignarCliente(@Param("dupId") Long dupId,
			@Param("masterId") Long masterId);

	@Query(value = "SELECT * FROM lecturas WHERE idrutaxemision_rutasxemision IN (:ids) ORDER BY idlectura DESC", nativeQuery = true)
	List<Lecturas> findByRutasxEmisionIds(@Param("ids") List<Long> ids);

	@Query(value = """
				SELECT l.*
				FROM lecturas l
				WHERE l.idrutaxemision_rutasxemision IN (
				    SELECT DISTINCT CAST(COALESCE(r->>'idrutaxemision', r->>'idrutaxemision_rutasxemision') AS bigint)
				    FROM usrxrutas u
				    CROSS JOIN LATERAL jsonb_array_elements(u.rutas) r
				    WHERE u.idusuario_usuarios = :idusuario
				      AND u.idemision_emisiones = :idemision
				      AND COALESCE(r->>'idrutaxemision', r->>'idrutaxemision_rutasxemision') ~ '^[0-9]+$'
				    UNION
				    SELECT DISTINCT rx.idrutaxemision
				    FROM usrxrutas u
				    CROSS JOIN LATERAL jsonb_array_elements(u.rutas) r
				    JOIN rutasxemision rx
				      ON rx.idemision_emisiones = :idemision
				     AND (r->>'idruta') ~ '^[0-9]+$'
				     AND rx.idruta_rutas = CAST(r->>'idruta' AS bigint)
				    WHERE u.idusuario_usuarios = :idusuario
				      AND u.idemision_emisiones = :idemision
				)
				ORDER BY l.idlectura DESC
			""", nativeQuery = true)
	List<Lecturas> findByUsuarioEmision(@Param("idusuario") Long idusuario, @Param("idemision") Long idemision);

	@Query(value = """
			    SELECT DISTINCT
			           l.idfactura AS idfactura,
			           l.idabonado_abonados    AS cuenta
			    FROM lecturas l
			    JOIN facturas f ON f.idfactura = l.idfactura
			    WHERE l.idemision = :idemision
			      AND l.idrutaxemision_rutasxemision = :idrutaxemision
			      AND f.pagado = 0
			      AND l.idfactura IS NOT NULL
			""", nativeQuery = true)
	List<FacturaCuentaView> findFacturasPendientesByEmisionAndRutaXEmision(
			@Param("idemision") Long idemision,
			@Param("idrutaxemision") Long idrutaxemision);

	@Modifying
	@Transactional
	@Query(value = """
			UPDATE rubroxfac r
			SET estado = 0
			FROM lecturas l
			WHERE l.idfactura = r.idfactura_facturas
			  AND l.idemision = :idemision
			""", nativeQuery = true)
	int eliminarRubrosByEmision(@Param("idemision") Long idemision);
}



