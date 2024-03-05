package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.FacturasI;
import com.epmapat.erp_epmapat.modelo.Facturas;

// @Repository
public interface FacturasR extends JpaRepository<Facturas, Long> {

	@Query(value = "SELECT * FROM facturas order by idfactura DESC LIMIT 12", nativeQuery = true)
	public List<Facturas> findAll();

	@Query(value = "SELECT * FROM facturas AS f WHERE f.idfactura >= ?1 and f.idfactura <= ?2 ", nativeQuery = true)
	public List<Facturas> findDesde(Long desde, Long hasta);

	// Planillas por Cliente
	@Query(value = "SELECT * FROM facturas WHERE idcliente=?1 ORDER BY idfactura DESC LIMIT 12", nativeQuery = true)
	public List<Facturas> findByIdcliente(Long idcliente);

	// Planillas por Abonado
	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY idfactura DESC LIMIT 15", nativeQuery = true)
	public List<Facturas> findByIdabonado(Long idabonado);

	// Planillas por Cliente (sinCobrar)
	// @Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1
	// and fechaeliminacion is null and fechaanulacion is null and ((estado = 2 and
	// pagado = 0) or estado = 0 or estado = 3 )ORDER BY idabonado, idfactura",
	// nativeQuery=true)
	// @Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1
	// and fechaconvenio is null and fechacobro is null and fechaeliminacion is null
	// and fechaanulacion is null ORDER BY idabonado, idfactura", nativeQuery=true)
	//@Query(value = "select * from facturas where (estado = 3 or ((estado = 2 or estado = 1 ) and pagado = 0 and fechacobro is null) and fechaanulacion is null and fechaeliminacion is null and fechaconvenio is null) and totaltarifa > 0 and idcliente = ?1 ORDER BY idabonado, idfactura", nativeQuery = true)
	@Query(value = "SELECT * FROM facturas WHERE totaltarifa > 0 and idcliente=?1 and (( (estado = 1 or estado = 2) and fechacobro is null) or estado = 3 ) and fechaconvenio is null and fechaanulacion is null and fechaeliminacion is null ORDER BY idabonado, idfactura", nativeQuery=true)
	public List<Facturas> findSinCobro(Long idcliente);

	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY nrofactura", nativeQuery = true)
	public List<Facturas> findByIdFactura(Long idabonado);

	// VALIDACION DE LA ULTIMA FACTURA DEL RECAUDADOR
	@Query(value = "select *, substring(nrofactura, 9) as nrofac from facturas where nrofactura like %?1% and not nrofactura  is null order by nrofac desc limit 1;", nativeQuery = true)
	public Facturas validarUltimafactura(String codrecaudador);

	@Query(nativeQuery = true, value = "select * from facturas f where f.usuariocobro = ?1 and (f.fechacobro between ?2 and ?3)")
	public List<Facturas> findByUsucobro(Long idusuario, Date dfecha, Date hfecha);

	@Query(nativeQuery = true, value = "select * from facturas f where f.fechacobro = ?1 ")
	public List<FacturasI> findByFechacobro(Date fechacobro);
}
