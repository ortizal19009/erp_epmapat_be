package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Facturas;

// @Repository
public interface FacturasR extends JpaRepository<Facturas, Long>{

	@Query(value = "SELECT * FROM facturas order by idfactura DESC LIMIT 12", nativeQuery = true)
	public List<Facturas> findAll();

	@Query(value = "SELECT * FROM facturas AS f WHERE f.idfactura >= ?1 and f.idfactura <= ?2 ", nativeQuery = true)
	public List<Facturas> findDesde(Long desde, Long hasta);

	//Planillas por Cliente
	@Query(value = "SELECT * FROM facturas WHERE idcliente=?1 ORDER BY idfactura DESC LIMIT 12", nativeQuery=true)
	public List<Facturas> findByIdcliente(Long idcliente);
	
	//Planillas por Abonado
	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY idfactura DESC LIMIT 15", nativeQuery=true)
	public List<Facturas> findByIdabonado(Long idabonado);

	//Planillas por Cliente (sinCobrar)
	@Query(value = "SELECT * FROM facturas WHERE pagado = 0 and totaltarifa > 0 and estado = 1 and idcliente=?1 ORDER BY idabonado, idfactura DESC", nativeQuery=true)
	public List<Facturas> findSinCobro(Long idcliente);

	@Query(value = "SELECT * FROM facturas WHERE idabonado=?1 ORDER BY nrofactura", nativeQuery=true)
	public List<Facturas> findByIdFactura(Long idabonado);
	
}
