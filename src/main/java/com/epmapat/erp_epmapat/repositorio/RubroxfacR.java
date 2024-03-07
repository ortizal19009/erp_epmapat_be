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

	// Rubros de una Planilla (Sin rubro 165 (Iva del siim 'esiva'))
	@Query(value = "SELECT * FROM rubroxfac AS r WHERE r.idfactura_facturas=?1 and idrubro_rubros <> 165 order by idrubro_rubros", nativeQuery = true)
	public List<Rubroxfac> findByIdfactura1(Long idfactura);

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

	@Query(value = "SELECT * FROM rubroxfac rf JOIN facturas f ON rf.idfactura_facturas = f.idfactura WHERE f.fechacobro between ?1 and ?2", nativeQuery = true)
	public List<Rubroxfac> findByFecha(Date d, Date h);

	/* SIN COBRO 2.0 */
	@Query(value = "select * from rubroxfac rf join facturas f on rf.idfactura_facturas = f.idfactura where totaltarifa > 0 and idcliente=?1 and (( (f.estado = 1 or f.estado = 2) and f.fechacobro is null) or f.estado = 3 ) and f.fechaconvenio is null and f.fechaanulacion is null and f.fechaeliminacion is null ORDER BY f.idabonado, f.idfactura ", nativeQuery = true)
	public List<Rubroxfac> findSinCobroRF(Long cuenta);
}
