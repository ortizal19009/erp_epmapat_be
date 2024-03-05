package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Abonados;
// import com.epmapat.erp_epmapat.modelo.Clientes;

public interface AbonadosR extends JpaRepository<Abonados, Long> {

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente ORDER BY c.nombre ASC LIMIT 3000", nativeQuery = true)
	public List<Abonados> tmpTodos();

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente WHERE CAST(a.idabonado AS varchar) LIKE %?1% OR c.cedula LIKE %?1% OR LOWER(c.nombre) LIKE %?1% ORDER BY c.nombre ASC", nativeQuery = true)
	public List<Abonados> findAll(String consultaDatos);
	
	@Query(value = "SELECT * FROM abonados where idabonado = ?1", nativeQuery = true)
	public Abonados findOne(Long idabonado);

	// Abonado por ID (o sea por Cuenta con abonados/id)
	@Query(value = "SELECT * FROM abonados WHERE idabonado=?1", nativeQuery = true)
	public List<Abonados> getAbonadoByid(Long idabonado);

	// Abonado por Cuenta como parametro (para la recaudación)
	@Query(value = "SELECT * FROM abonados WHERE idabonado=?1", nativeQuery = true)
	public List<Abonados> getByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS C ON a.idcliente_clientes = c.idcliente WHERE LOWER(c.nombre) LIKE %?1% ORDER BY c.nombre ", nativeQuery = true)
	public List<Abonados> findByNombreCliente(String nombreCliente);

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS C ON a.idcliente_clientes = c.idcliente WHERE c.cedula LIKE %?1% ORDER BY c.nombre ", nativeQuery = true)
	public List<Abonados> findByidentIficacionCliente(String identificacion);

	// Cuentas de un Cliente
	@Query(value = "SELECT * FROM abonados WHERE idcliente_clientes=?1 ORDER BY idabonado", nativeQuery = true)
	public List<Abonados> findByIdcliente(Long idcliente);

	// Abonados de una Ruta
	@Query(value = "SELECT * FROM abonados as a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente WHERE a.estado = 1 and a.idruta_rutas=?1 ORDER BY c.nombre", nativeQuery = true)
	public List<Abonados> findByIdruta(Long idruta);

	@Query(value = "SELECT * FROM abonados WHERE estado=?1", nativeQuery = true)
	public List<Abonados> findByEstado(Long estado);

	//Cliente tiene Abonados
	@Query(value = "SELECT EXISTS (SELECT 1 FROM Abonados WHERE idcliente_clientes = ?1)", nativeQuery = true)
	boolean existsByIdcliente_clientes(Long idcliente);

	@Query(value = "SELECT * FROM abonados a JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE c.idcliente = ?1", nativeQuery = true)
	public List<Abonados> findByIdCliente(Long idcliente);

	 //Campos específicos de Clientes y Abonados
    @Query("SELECT new map(" +
	 			"a.idabonado as idabonado, " +
            "c.nombre as nombre, " + "c.cedula as cedula, " + "c.direccion as direccion, " + 
            "a.direccionubicacion as direccionubicacion, " + "c.telefono as telefono, " + "c.fechanacimiento as fechanacimiento, " +"c.email as email) " +
            "FROM Clientes c INNER JOIN Abonados a ON c.idcliente = a.idcliente_clientes")
    List<Map<String, Object>> allAbonadosCampos();
	 //Campos específicos de Clientes y Abonados
/*     @Query("SELECT new map(" +
	 			"a.idabonado as idabonado, " +
            "c.nombre as nombre, " + "c.cedula as cedula, " + "c.direccion as direccion, " + 
            "a.direccionubicacion as direccionubicacion, " + "c.telefono as telefono, " + "c.fechanacimiento as fechanacimiento, " +"c.email as email) " +
            "FROM Abonados a INNER JOIN Categorias a ON c.idcliente = a.idcliente_clientes")
    List<Map<String, Object>> getOneAbonado(Long idabonado); */
}
