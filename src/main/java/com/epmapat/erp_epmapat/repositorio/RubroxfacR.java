package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Rubroxfac;

public interface RubroxfacR extends JpaRepository<Rubroxfac, Long>{

	//Rubros de una Planilla por Nro. de Factura
	// @Query(value="SELECT * FROM rubroxfac WHERE idfactura_facturas=?1", nativeQuery=true)
	// public List<Rubroxfac> findByFactura(Long nrofactura);

	//Rubros de una Planilla
	@Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 order by idrubro_rubros", nativeQuery=true)
	public List<Rubroxfac> findByIdfactura(Long idfactura);

	//Rubroxfac de un Rubro (movimientos de un Rubro)
	@Query(value = "SELECT * FROM rubroxfac WHERE idrubro_rubros =?1 order by idrubroxfac desc limit 100", nativeQuery=true)
	public List<Rubroxfac> findByIdrubro(Long idrubro);
	
}
