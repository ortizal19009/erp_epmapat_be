package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.CVClientes;
import com.epmapat.erp_epmapat.modelo.Clientes;

public interface ClientesR extends JpaRepository<Clientes, Long> {

	// Clientes por Nombre o Identificacion
	@Query(value = "select * from clientes WHERE LOWER(nombre) LIKE %?1% OR cedula LIKE %?1% ORDER BY nombre", nativeQuery = true)
	List<Clientes> findByNombreIdentifi(String nombreIdentifi);

	// Valida Identificación del Cliente
	@Query("SELECT COUNT(c) > 0 FROM Clientes c WHERE c.cedula = :cedula")
	boolean valIdentificacion(@Param("cedula") String cedula);

	// Valida Nombre Cliente
	@Query("SELECT COUNT(c) > 0 FROM Clientes c WHERE LOWER(c.nombre) = :nombre")
	boolean valNombre(@Param("nombre") String nombre);

	// Cliente por Identificacion
	@Query(value = "SELECT * FROM clientes AS c WHERE c.cedula=?1", nativeQuery = true)
	List<Clientes> findByIdentificacion(String identificacion);

	// Clientes por Nombre ()
	@Query(value = "SELECT * FROM clientes AS c WHERE LOWER(c.nombre) LIKE %?1% order by nombre", nativeQuery = true)
	List<Clientes> findByNombre(String nombre);

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM clientes AS c WHERE NOT EXISTS(SELECT * FROM abonados AS a WHERE a.idcliente_clientes=c.idcliente)AND c.idcliente=?1 ", nativeQuery = true)
	void deleteByIdQ(Long id);

	@Query(value = "SELECT * FROM clientes AS c WHERE EXISTS(SELECT * FROM abonados AS a WHERE a.idcliente_clientes=c.idcliente)AND c.idcliente=?1 ", nativeQuery = true)
	List<Clientes> used(Long id);

	@Query("SELECT new map(c.idcliente as idcliente, c.nombre as nombre) FROM Clientes c order by idcliente")
	List<Map<String, Object>> findAllClientsFields();

	@Query(value = "select count(*) from clientes", nativeQuery = true)
	Long totalClientes();

	/* CARTERA VENCIDA */
	@Query(value = "select rf.idfactura_facturas as planilla, c.nombre, sum(rf.cantidad * rf.valorunitario) as valor, c.cedula , c.direccion, c.email, m.descripcion as modulo"
+" from clientes c "
+" join facturas f on c.idcliente = f.idcliente "
+" join rubroxfac rf on rf.idfactura_facturas = f.idfactura  "
+" join modulos m on f.idmodulo = m.idmodulo "
+" where f.totaltarifa > 0 and (( (f.estado = 1 or f.estado = 2) and ( f.fechacobro > '2024-11-20' or f.fechacobro is null)) or f.estado = 3 )"
+" and f.fechaconvenio is null and f.fechaeliminacion is null"
+" group by rf.idfactura_facturas, c.nombre, c.cedula , c.direccion , c.email, m.descripcion order by c.nombre asc", nativeQuery = true)
List<CVClientes>getCVByCliente(LocalDate fecha);

}
