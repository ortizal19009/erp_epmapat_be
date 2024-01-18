package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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

}
