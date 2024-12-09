package com.epmapat.erp_epmapat.repositorio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.scheduling.annotation.Async;

import com.epmapat.erp_epmapat.interfaces.ConsumoxCat_int;
import com.epmapat.erp_epmapat.interfaces.CountRubrosByEmision;
import com.epmapat.erp_epmapat.interfaces.FacIntereses;
import com.epmapat.erp_epmapat.interfaces.FecEmision;
import com.epmapat.erp_epmapat.interfaces.RepEmisionEmi;
import com.epmapat.erp_epmapat.interfaces.RubroxfacIReport;
import com.epmapat.erp_epmapat.modelo.Lecturas;

public interface LecturasR extends JpaRepository<Lecturas, Long> {

	// Lectura por Planilla (Es una a una)
	@Query(value = "SELECT * FROM lecturas WHERE idfactura=?1 ", nativeQuery = true)
	public Lecturas findOnefactura(Long idfactura);

	// Lecturas por rutasxemision
	@Query(value = "SELECT * FROM lecturas WHERE idrutaxemision_rutasxemision=?1 order by idabonado_abonados", nativeQuery = true)
	public List<Lecturas> findByIdrutaxemision(Long idrutasxemision);

	// Lecturas por Abonado (Historial de consumo)
	@Query(value = "SELECT * FROM lecturas WHERE idabonado_abonados=?1 ORDER BY idlectura DESC LIMIT 12", nativeQuery = true)
	public List<Lecturas> findByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM lecturas WHERE mesesmulta>=4 and estado=1 LIMIT 20", nativeQuery = true)
	public List<Lecturas> findByMonth();

	@Query(value = "SELECT * FROM lecturas WHERE idabonado_abonados=?1 ", nativeQuery = true)
	public List<Lecturas> findLecturasByIdAbonados(Long idabonado);

	@Query(value = "SELECT * FROM lecturas WHERE idrutaxemision_rutasxemision = ?1", nativeQuery = true)
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
	@Query(value = "SELECT * FROM lecturas WHERE idfactura=?1 ", nativeQuery = true)
	public List<Lecturas> findByIdfactura(Long idfactura);

	// Lecturas de una Emisión
	@Query(value = "SELECT * FROM lecturas WHERE idemision=?1 ", nativeQuery = true)
	public List<Lecturas> findByIdemision(Long idemision);

	// Lecturas de una Emisión
	@Query(value = "SELECT * FROM lecturas WHERE idemision=?1 and idabonado_abonados = ?2 order by idlectura desc", nativeQuery = true)
	public List<Lecturas> findByIdemisionIdAbonado(Long idemision, Long idabonado);

	// Ultima lectura de un Abonado: debe ser lecturaactual tempoaralmente
	// lecturaanterior porque no están cerradas las rutas de la emisión anterior
	@Query(value = "SELECT l.lecturaactual FROM lecturas l WHERE l.idabonado_abonados=?1 ORDER BY l.idemision DESC LIMIT 1", nativeQuery = true)
	public Long ultimaLectura(Long idabonado);

	@Query(value = "SELECT l.lecturaactual FROM lecturas l WHERE l.idabonado_abonados=?1 and l.idemision =?2 ORDER BY l.idemision DESC LIMIT 1", nativeQuery = true)
	public Long ultimaLecturaByIdemision(Long idabonado, long idemision);

	@Query(value = "select sum(f.totaltarifa) from lecturas l join facturas f on l.idfactura = f.idfactura where l.idemision = ?1", nativeQuery = true)
	public BigDecimal totalEmisionXFactura(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas join rubros r on rf.idrubro_rubros = r.idrubro where l.idemision = ?1 group by r.idrubro", nativeQuery = true)
	public List<Object[]> RubrosEmitidos(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas join rubros r on rf.idrubro_rubros = r.idrubro where not f.fechaeliminacion is null and not f.usuarioeliminacion is null and  l.idemision = ?1 group by r.idrubro", nativeQuery = true)
	public List<Object[]> R_EmisionFinal(Long idemision);

	@Query(value = "select r.idrubro, r.descripcion, sum(rf.cantidad * rf.valorunitario) from lecturas l join facturas f on l.idfactura = f.idfactura join rubroxfac rf on f.idfactura = rf.idfactura_facturas join rubros r on rf.idrubro_rubros = r.idrubro where f.fechaeliminacion  is null and f.usuarioeliminacion is null and  l.idemision = ?1 group by r.idrubro;", nativeQuery = true)
	public List<Object[]> R_EmisionActual(Long idemision);

	/* REPORTE DEUDORES */
	@Query(value = "select * from lecturas l join facturas f on l.idfactura = f.idfactura join rutasxemision re on l.idrutaxemision_rutasxemision = re.idrutaxemision join rutas r on re.idruta_rutas = r.idruta  where f.pagado = 0 and f.fechaeliminacion is null and r.idruta = ?1 and f.estadoconvenio = 0", nativeQuery = true)
	public List<Lecturas> findDeudoresByRuta(Long ruta);

	/* encontrar fecha de emision para recaudacion */
	@Query(value = "select e.feccrea from lecturas l join emisiones e on l.idemision = e.idemision where idfactura = ?1", nativeQuery = true)
	public Date findDateByIdfactura(Long idfactura);

	@Query(value = "select e.emision, e.feccrea from lecturas l join emisiones e on l.idemision = e.idemision where l.idfactura =  ?1", nativeQuery = true)
	public List<FecEmision> getEmisionByIdfactura(Long idfactura);

	@Query(value = "SELECT * FROM emisiones e join lecturas l on e.idemision = l.idemision join facturas f on l.idfactura = f.idfactura where not f.fechaeliminacion is null and l.idemision = ?1 order by f.idabonado", nativeQuery = true)
	List<Lecturas> findByIdEmisiones(Long idemision);

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
			+ "FROM lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas join rubros r on rf.idrubro_rubros = r.idrubro "
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
			+ "join rubroxfac rf on l.idfactura = rf.idfactura_facturas "
			+ "join facturas f on rf.idfactura_facturas = f.idfactura "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro  "
			+ "where ei.idemision = ?1 and f.fechaeliminacion is null and not rf.idrubro_rubros = 5 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllNewLecturas(Long idemision);

	/*
	 * --REPORTE DE LOS RUBROS ELIMINADOS
	 */
	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(l.idabonado_abonados) as abonados "
			+ "from emisionindividual ei "
			+ "join lecturas l on ei.idlecturaanterior = l.idlectura "
			+ "join rubroxfac rf on l.idfactura = rf.idfactura_facturas "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro  "
			+ "where ei.idemision = ?1 and not rf.idrubro_rubros = 5 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllDeleteLecturas(Long idemision);

	@Query(value = "select rf.idrubro_rubros , r.descripcion , sum(rf.cantidad * rf.valorunitario) as total , count(l.idabonado_abonados) as abonados "
			+ "from lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas "
			+ "join facturas f on rf.idfactura_facturas = f.idfactura "
			+ "join rubros r on rf.idrubro_rubros  = r.idrubro "
			+ "where l.idemision = ?1 and f.fechaeliminacion is null and not rf.idrubro_rubros = 5 "
			+ "group by rf.idrubro_rubros , r.descripcion ", nativeQuery = true)
	public CompletableFuture<List<RubroxfacIReport>> getAllActual(Long idemision);

	@Query(value = "select rf.idfactura_facturas as idfactura, sum(rf.cantidad * rf.valorunitario) as suma, e.feccrea from lecturas l join rubroxfac rf on l.idfactura = rf.idfactura_facturas join emisiones e on l.idemision = e.idemision join facturas f on l.idfactura = f.idfactura where l.idfactura = ?1 and not (rf.idrubro_rubros = 165 or rf.idrubro_rubros = 5 ) group by rf.idfactura_facturas, e.feccrea", nativeQuery = true)
	public List<FacIntereses> getForIntereses(Long idfactura);

	@Query(value = "select cl.nombre, a.idabonado as cuenta, sum(rf.cantidad * rf.valorunitario) as valEmitido, c.descripcion as categoria, l.lecturaactual - l.lecturaanterior as m3 from lecturas l join clientes cl on l.idresponsable = cl.idcliente join abonados a on l.idabonado_abonados = a.idabonado join rubroxfac rf on rf.idfactura_facturas = l.idfactura join categorias c on l.idcategoria = c.idcategoria where idemision = ?1 and not rf.idrubro_rubros = 6 and l.observaciones is null group by a.idabonado, c.descripcion, cl.idcliente, l.lecturaanterior, l.lecturaactual order by a.idabonado asc", nativeQuery = true)
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
" SELECT a.idabonado_abonados,l.idfactura, l.lecturaanterior, l.lecturaactual, COUNT(rf.idrubro_rubros) AS rubros_count" +
" FROM abonados_con_emision a" +
" LEFT JOIN lecturas l" +
" ON a.idabonado_abonados = l.idabonado_abonados AND l.idemision = ?1" +
" LEFT JOIN rubroxfac rf" +
" ON rf.idfactura_facturas = l.idfactura" +
" GROUP BY a.idabonado_abonados,l.idfactura, l.lecturaanterior, l.lecturaactual " +
"HAVING COUNT(rf.idrubro_rubros) = 0 ", nativeQuery = true)
public List<CountRubrosByEmision>getCuentaRubrosByEmision(long idemision );

}
