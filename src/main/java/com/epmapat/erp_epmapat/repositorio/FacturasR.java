package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.FacturasI;
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

	// Una Planilla (como lista para mostrar en la misma forma que por Abonado)
	public List<Facturas> findByIdfactura(Long idfactura);

	// Planillas por Abonado y Fecha
	@Query("SELECT f FROM Facturas f WHERE f.idabonado = :idabonado AND f.feccrea BETWEEN :fechaDesde AND :fechaHasta AND totaltarifa > 0 order by feccrea desc")
	List<Facturas> findByAbonadoAndFechaCreacionRange(@Param("idabonado") Long idabonado,
			@Param("fechaDesde") LocalDate fechaDesde,
			@Param("fechaHasta") LocalDate fechaHasta);

	// Planillas por Cliente (sinCobrar)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idabonado, idfactura", nativeQuery = true)
	public List<Facturas> findSinCobro(Long idcliente);

	// Planillas por Abonado
	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY nrofactura", nativeQuery = true)
	public List<Facturas> findByIdFactura(Long idabonado);

	// Planilla por nrofactura
	@Query(value = "SELECT * FROM facturas WHERE nrofactura=?1 order by idfactura", nativeQuery = true)
	public List<Facturas> findByNrofactura(String nrofactura);

	// ID de las Planillas Sin cobrar por Abonado (para Multas)
	@Query(value = "SELECT idfactura FROM facturas WHERE totaltarifa > 0 and idabonado=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idfactura", nativeQuery = true)
	public List<Long> findSinCobroAbo(Long idabonado);

	// Planillas Sin cobrar por modulo y Abonado (para Convenios)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idmodulo=:idmodulo and idabonado=:idabonado and estado = 1 and fechacobro is null and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idfactura", nativeQuery = true)
	public List<Facturas> findSinCobrarAbo(Long idmodulo, Long idabonado);

	// Recaudacion diaria - Facturas cobradas <Facturas>
	// @Query(value = "SELECT * FROM facturas WHERE (fechacobro = ?1 or
	// fechatransferencia=?1) and fechaanulacion is null and fechaeliminacion is
	// null ORDER BY idfactura", nativeQuery = true)
	// public List<Facturas> findByFechacobro(LocalDate fecha);
	// Recaudacion diaria - Facturas cobradas
	@Query("SELECT f, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f " +
			"JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2) AND NOT f.estado = 3 AND f.fechaeliminacion IS NULL AND f.fechaanulacion IS NULL "
			+
			"GROUP BY f.idfactura, f.nrofactura  ORDER BY f.nrofactura")
	List<Object[]> findByFechacobroTot(LocalDate d_fecha, LocalDate h_fecha);

	@Query("SELECT f, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f " +
			"JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura " +
			"WHERE (date(f.fechacobro) BETWEEN ?1 AND ?2) AND NOT f.estado = 3 AND f.usuariocobro = ?3 AND f.fechaeliminacion IS NULL AND f.fechaanulacion IS NULL "
			+
			"GROUP BY f.idfactura, f.nrofactura  ORDER BY f.nrofactura")
	List<Object[]> findByFechacobroTotByRecaudador(LocalDate d_fecha, LocalDate h_fecha, Long idrecaudador);

	// Total diario por Forma de cobro
	@Query(value = "SELECT fc.descripcion AS formaCobro, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f "
			+ "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura "
			+ "JOIN Formacobro fc ON fc.idformacobro = f.formapago "
			+ "WHERE (f.fechacobro BETWEEN ?1 and ?2) AND NOT f.estado = 3  AND f.fechaeliminacion IS NULL AND f.fechaconvenio IS NULL GROUP BY fc.descripcion ORDER BY fc.descripcion")
	List<Object[]> totalFechaFormacobro(@Param("d_fecha") LocalDate d_fecha, @Param("d_fecha") LocalDate h_fecha);

	@Query(value = "SELECT fc.descripcion AS formaCobro, SUM(rf.cantidad * rf.valorunitario) AS total FROM Facturas f "
			+ "JOIN Rubroxfac rf ON rf.idfactura_facturas = f.idfactura "
			+ "JOIN Formacobro fc ON fc.idformacobro = f.formapago "
			+ "WHERE (f.fechacobro BETWEEN ?1 and ?2) AND NOT f.estado = 3 AND f.usuariocobro = ?3 AND f.fechaeliminacion IS NULL AND f.fechaconvenio IS NULL GROUP BY fc.descripcion  ORDER BY fc.descripcion")
	List<Object[]> totalFechaFormacobroByRecaudador(@Param("d_fecha") LocalDate d_fecha,
			@Param("d_fecha") LocalDate h_fecha, @Param("recaudador") Long idrecaudador);

	// Cuenta las Facturas pendientes de un Abonado
	@Query("SELECT COUNT(*) FROM Facturas f WHERE f.totaltarifa > 0 and f.idabonado=:idabonado and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaanulacion is null and f.fechaeliminacion is null")
	long countFacturasByAbonadoAndPendientes(@Param("idabonado") Long idabonado);
}
