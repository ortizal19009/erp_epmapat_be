package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.RubroxfacI;
import com.epmapat.erp_epmapat.modelo.Rubroxfac;

public interface RubroxfacR extends JpaRepository<Rubroxfac, Long> {

	// Rubros de una Planilla por Nro. de Factura
	// @Query(value="SELECT * FROM rubroxfac WHERE idfactura_facturas=?1",
	// nativeQuery=true)
	// public List<Rubroxfac> findByFactura(Long nrofactura);

	// Rubros de una Planilla
	@Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 order by idrubro_rubros", nativeQuery = true)
	public List<Rubroxfac> findByIdfactura(Long idfactura);

	// Rubroxfac de un Rubro (movimientos de un Rubro)
	@Query(value = "SELECT * FROM rubroxfac WHERE idrubro_rubros =?1 order by idrubroxfac desc limit 100", nativeQuery = true)
	public List<Rubroxfac> findByIdrubro(Long idrubro);

	// Campos específicos: Rubro y Valor de una Factura (Planilla)
	@Query("SELECT new map(" +
			"r.descripcion as descripcion, " + "rf.valorunitario as valorunitario) " +
			"FROM Rubroxfac rf INNER JOIN Rubros r ON r.idrubro = rf.idrubro_rubros WHERE rf.idfactura_facturas=?1 order by rf.idrubro_rubros")
	List<Map<String, Object>> rubrosByIdfactura(Long idfactura);

	@Query(value = "select sum(valorunitario)  from rubroxfac r where idfactura_facturas = ?1", nativeQuery = true)
	Double findSuma(Long idfactura);

	/*
	 * @Query(value =
	 * "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros"
	 * , nativeQuery = true)
	 */
	@Query(value = "select rf.idrubro_rubros , sum(rf.valorunitario) from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where f.fechacobro = ?1 group by rf.idrubro_rubros ", nativeQuery = true)
	List<RubroxfacI> getByFechaCobro(Date d, Date h);

	@Query(value = "SELECT * FROM rubrosxfac rf JOIN facturas rf.idfactura_facturas ON f.idfactura WHERE f.fechacobro between ?1 and ?2", nativeQuery = true)
	public List<Rubroxfac> findByFecha(Date d, Date h);
 

}
