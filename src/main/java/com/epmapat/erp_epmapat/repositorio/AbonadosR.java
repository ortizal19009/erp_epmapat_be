package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.AbonadoI;
import com.epmapat.erp_epmapat.interfaces.EstadisticasAbonados;
import com.epmapat.erp_epmapat.interfaces.mobile.AbonadosMobile;
import com.epmapat.erp_epmapat.modelo.Abonados;
// import com.epmapat.erp_epmapat.modelo.Clientes;

public interface AbonadosR extends JpaRepository<Abonados, Long> {

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente ORDER BY c.nombre ASC LIMIT 3000", nativeQuery = true)
	public List<Abonados> tmpTodos();

	@Query(value = "SELECT * FROM abonados AS a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente WHERE CAST(a.idabonado AS varchar) LIKE %?1% OR c.cedula LIKE %?1% OR LOWER(c.nombre) LIKE %?1% ORDER BY c.nombre ASC", nativeQuery = true)
	public List<Abonados> findAll(String consultaDatos);

	@Query(value = "SELECT * FROM abonados where idabonado = ?1", nativeQuery = true)
	public Abonados findOne(Long idabonado);

	@Query(value = "SELECT a.idabonado, c.nombre, c.cedula as identificacion, ct.descripcion as categoria, r.descripcion as ruta, a.direccionubicacion as direccion, a.estado from abonados a join clientes c on c.idcliente = a.idcliente_clientes join categorias ct on ct.idcategoria = a.idcategoria_categorias join rutas r on a.idruta_rutas = r.idruta where a.idabonado = ?1", nativeQuery = true)
	public List<AbonadoI> getAbonadoInterface(Long idabonado);

	@Query(value = "SELECT a.idabonado, c.nombre, c.cedula as identificacion, ct.descripcion as categoria, r.descripcion as ruta, a.direccionubicacion as direccion, a.estado from abonados a join clientes c on c.idcliente = a.idcliente_clientes join categorias ct on ct.idcategoria = a.idcategoria_categorias join rutas r on a.idruta_rutas = r.idruta where LOWER(c.nombre) LIKE %?1% ", nativeQuery = true)
	public List<AbonadoI> getAbonadoInterfaceNombre(String nombre);

	@Query(value = "SELECT a.idabonado, c.nombre, c.cedula as identificacion, ct.descripcion as categoria, r.descripcion as ruta, a.direccionubicacion as direccion, a.estado from abonados a join clientes c on c.idcliente = a.idcliente_clientes join categorias ct on ct.idcategoria = a.idcategoria_categorias join rutas r on a.idruta_rutas = r.idruta where LOWER(c.cedula) LIKE %?1% ", nativeQuery = true)
	public List<AbonadoI> getAbonadoInterfaceIdentificacion(String identificacion);

	@Query(value = "SELECT a.idabonado, c.nombre, c.cedula as identificacion, ct.descripcion as categoria, r.descripcion as ruta, a.direccionubicacion as direccion, a.estado from abonados a join clientes c on c.idcliente = a.idcliente_clientes join categorias ct on ct.idcategoria = a.idcategoria_categorias join rutas r on a.idruta_rutas = r.idruta where c.idcliente = ?1 ", nativeQuery = true)
	public List<AbonadoI> getAbonadoInterfaceIdCliente(Long idcliente);

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
	@Query(value = "SELECT * FROM abonados as a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente WHERE (a.estado = 1 or a.estado = 2) and a.idruta_rutas=?1 ORDER BY c.nombre", nativeQuery = true)
	public List<Abonados> findByIdruta(Long idruta);

	@Query(value = "SELECT * FROM abonados as a JOIN clientes AS c ON a.idcliente_clientes = c.idcliente WHERE (a.estado = 1 or a.estado = 2) and a.idruta_rutas=?1 ORDER BY c.nombre", nativeQuery = true)
	public Page<Abonados> findByIdruta(Long idruta, Pageable pageable);

	// Abonados de una Categoria
	@Query(value = "SELECT * FROM abonados WHERE idcategoria_categorias=?1", nativeQuery = true)
	public List<Abonados> findByIdcategoria(Long idcategoria);

	@Query(value = "SELECT * FROM abonados WHERE idcategoria_categorias=?1", nativeQuery = true)
	public Page<Abonados> findByIdcategoria(Long idcategoria, Pageable pageable);

	@Query(value = "SELECT * FROM abonados WHERE estado=?1", nativeQuery = true)
	public List<Abonados> findByEstado(Long estado);

	@Query(value = "SELECT * FROM abonados WHERE estado=?1", nativeQuery = true)
	public Page<Abonados> findByEstado(Long estado, Pageable pageable);

	// Cliente tiene Abonados
	@Query(value = "SELECT EXISTS (SELECT 1 FROM Abonados WHERE idcliente_clientes = ?1)", nativeQuery = true)
	boolean existsByIdcliente_clientes(Long idcliente);

	@Query(value = "SELECT * FROM abonados a JOIN clientes c ON a.idcliente_clientes = c.idcliente WHERE c.idcliente = ?1", nativeQuery = true)
	public List<Abonados> findByIdCliente(Long idcliente);

	// Campos específicos de Clientes y Abonados
	@Query("SELECT new map(" +
			"a.idabonado as idabonado, " +
			"c.nombre as nombre, " + "c.cedula as cedula, " + "c.direccion as direccion, " +
			"a.direccionubicacion as direccionubicacion, " + "c.telefono as telefono, "
			+ "c.fechanacimiento as fechanacimiento, " + "c.email as email) " +
			"FROM Clientes c INNER JOIN Abonados a ON c.idcliente = a.idcliente_clientes")
	List<Map<String, Object>> allAbonadosCampos();

	// Campos específicos de Clientes y Abonados
	/*
	 * @Query("SELECT new map(" +
	 * "a.idabonado as idabonado, " +
	 * "c.nombre as nombre, " + "c.cedula as cedula, " +
	 * "c.direccion as direccion, " +
	 * "a.direccionubicacion as direccionubicacion, " + "c.telefono as telefono, " +
	 * "c.fechanacimiento as fechanacimiento, " +"c.email as email) " +
	 * "FROM Abonados a INNER JOIN Categorias a ON c.idcliente = a.idcliente_clientes"
	 * )
	 * List<Map<String, Object>> getOneAbonado(Long idabonado);
	 */
	// Un Abonado
	Abonados findByIdabonado(Long idabonado);

	@Query(value = "SELECT * FROM abonados a where a.idruta_rutas = ?1 order by a.idabonado asc", nativeQuery = true)
	public List<Abonados> getCuentasByRutas(Long idruta);

	@Query(value = "select a.idcategoria_categorias, c.descripcion , count(*) as ncuentas from abonados a join categorias c on a.idcategoria_categorias = c.idcategoria group by a.idcategoria_categorias, c.descripcion", nativeQuery = true)
	public List<EstadisticasAbonados> getCuentasByCategoria();

	@Query(value = "select a.estado, count(*) as ncuentas from abonados a group by a.estado", nativeQuery = true)
	public List<EstadisticasAbonados> getCuentasByEstado();

	@Modifying
	@Transactional
	@Query(value = """
			  UPDATE abonados
			  SET idcliente = :masterId
			  WHERE idcliente = :dupId
			""", nativeQuery = true)
	void reasignarCliente(@Param("dupId") Long dupId,
			@Param("masterId") Long masterId);

	@Query("""
			    SELECT a
			    FROM Abonados a
			    WHERE
			        (:idruta IS NULL OR a.idruta_rutas.idruta = :idruta)
			    AND (:estado IS NULL OR a.estado = :estado)
			    AND (
			        :responsable IS NULL OR
			        LOWER(a.idresponsable.nombre) LIKE LOWER(CONCAT('%', :responsable, '%'))
			    )
				AND (
			        :cedula IS NULL OR
			        LOWER(a.idresponsable.cedula) LIKE CONCAT('%', :cedula, '%')
			    )
			AND (
			        :cuenta IS NULL OR
			        a.idabonado = :cuenta
			    )
			AND (
			        :ruta IS NULL OR
			        LOWER(a.idruta_rutas.descripcion) = :ruta
			    )
			""")
	Page<Abonados> buscarConFiltros(
			@Param("idruta") Long idruta,
			@Param("responsable") String responsable,
			@Param("estado") Long estado,
			@Param("cedula") String cedula,
			@Param("cuenta") Long cuenta,
			@Param("ruta") String ruta,
			Pageable pageable);

	/*
	 * =============================================================
	 * QUERYS PARA MOBILE
	 * =============================================================
	 */
	@Query(value = "SELECT a.idabonado AS idabonado, a.nromedidor AS nromedidor, a.estado AS estado, a.fechainstalacion AS fechainstalacion, a.direccionubicacion AS direccionubicacion, a.observacion AS observacion, a.idresponsable AS idresponsable, a.idcategoria_categorias AS idcategoria_categorias, a.idruta_rutas AS idruta_rutas, a.idcliente_clientes AS idcliente_clientes, a.idestadom_estadom AS idestadom_estadom, a.municipio AS municipio, a.adultomayor AS adultomayor, a.swalcantarillado AS swalcantarillado, a.geolocalizacion AS geolocalizacion FROM abonados a", nativeQuery = true)
	List<AbonadosMobile> getAllAbonadosMobile();

	@Query(value = "SELECT a.idabonado AS idabonado, a.nromedidor AS nromedidor, a.estado AS estado, a.fechainstalacion AS fechainstalacion, a.direccionubicacion AS direccionubicacion, a.observacion AS observacion, a.idresponsable AS idresponsable, a.idcategoria_categorias AS idcategoria_categorias, a.idruta_rutas AS idruta_rutas, a.idcliente_clientes AS idcliente_clientes, a.idestadom_estadom AS idestadom_estadom, a.municipio AS municipio, a.adultomayor AS adultomayor, a.swalcantarillado AS swalcantarillado, a.geolocalizacion AS geolocalizacion FROM abonados a WHERE a.idruta_rutas IN (:idrutas) ORDER BY a.idruta_rutas, a.idabonado", nativeQuery = true)
	List<AbonadosMobile> getAbonadosMobileByRutas(@Param("idrutas") List<Long> idrutas);

}
