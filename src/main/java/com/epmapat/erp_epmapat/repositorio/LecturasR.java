package com.epmapat.erp_epmapat.repositorio;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

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
}
