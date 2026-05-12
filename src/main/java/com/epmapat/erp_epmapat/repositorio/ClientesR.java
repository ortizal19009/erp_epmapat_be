package com.epmapat.erp_epmapat.repositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import javax.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.CVClientes;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoGrupoView;
import com.epmapat.erp_epmapat.interfaces.ClienteDuplicadoView;
import com.epmapat.erp_epmapat.interfaces.mobile.ClientesMobile;
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
	@Query(value = "select rf.idfactura_facturas as planilla, c.idcliente, c.nombre, sum(rf.cantidad * rf.valorunitario) as valor, c.cedula , c.direccion, c.email, m.descripcion as modulo"
			+ " from clientes c "
			+ " join facturas f on c.idcliente = f.idcliente "
			+ " join rubroxfac rf on rf.idfactura_facturas = f.idfactura and (rf.estado = 1 or rf.estado is null)  "
			+ " join rubros r on r.idrubro = rf.idrubro_rubros "
			+ " join modulos m on f.idmodulo = m.idmodulo "
			+ " where f.totaltarifa > 0 and (( (f.estado = 1 or f.estado = 2) and f.feccrea <= ?1 and ( f.fechacobro >= ?1 or f.fechacobro is null)) or f.estado = 3 )"
			+ " and f.fechaconvenio is null and f.fechaeliminacion is null AND rf.idrubro_rubros NOT IN (79, 5, 165)"
			+ " and (rf.estado = 1 or rf.estado is null) "
			+ " group by rf.idfactura_facturas, c.nombre, c.cedula , c.direccion , c.email, c.idcliente, m.descripcion order by c.nombre asc", nativeQuery = true)
	List<CVClientes> getCVByCliente(LocalDate fecha);

	/* CARTERA VENCIDA */
	@Query(value = """
			select
			c.idcliente,
				c.nombre,
				sum(rf.cantidad * rf.valorunitario) as valor,
				c.cedula ,
				c.direccion,
				c.email,
				c.telefono
			from
				clientes c
			join facturas f on
				c.idcliente = f.idcliente
			join rubroxfac rf on
				rf.idfactura_facturas = f.idfactura and (rf.estado = 1 or rf.estado is null)
			join rubros r on
				r.idrubro = rf.idrubro_rubros
			where
				f.totaltarifa > 0
				and (( (f.estado = 1
					or f.estado = 2)
				and f.feccrea <= ?1
				and ( f.fechacobro >= ?1
					or f.fechacobro is null))
				or f.estado = 3 )
				and f.fechaconvenio is null
				and f.fechaeliminacion is null
			AND rf.idrubro_rubros NOT IN (79, 5, 165)
			AND (rf.estado = 1 or rf.estado is null)
			group by
			c.idcliente,
				c.nombre,
				c.cedula ,
				c.direccion ,
				c.email,
				c.telefono
			order by
				valor desc
			""", countQuery = """
				select
					count(*)
				from (
					select 1
					from clientes c
					join facturas f on c.idcliente = f.idcliente
					join rubroxfac rf on rf.idfactura_facturas = f.idfactura and (rf.estado = 1 or rf.estado is null)
					join rubros r on r.idrubro = rf.idrubro_rubros
					where
						f.totaltarifa > 0
						and (( (f.estado = 1
							or f.estado = 2)
						and f.feccrea <= ?1
						and ( f.fechacobro >= ?1
							or f.fechacobro is null))
						or f.estado = 3 )
						and f.fechaconvenio is null
						and f.fechaeliminacion is null
						and (rf.estado = 1 or rf.estado is null)
					group by
					c.idcliente,
						c.nombre,
						c.cedula ,
						c.direccion ,
						c.email,
						c.telefono
				) as sub
			""", nativeQuery = true)
	Page<CVClientes> getCVOfClientes(LocalDate fecha, Pageable pageable);

	/* CARTERA VENCIDA */
	@Query(value = """
			select
			c.idcliente,
				c.nombre,
				sum(rf.cantidad * rf.valorunitario) as valor,
				c.cedula ,
				c.direccion,
				c.email,
				c.telefono
			from
				clientes c
			join facturas f on
				c.idcliente = f.idcliente
			join rubroxfac rf on
				rf.idfactura_facturas = f.idfactura and (rf.estado = 1 or rf.estado is null)
			join rubros r on
				r.idrubro = rf.idrubro_rubros
			where
			(LOWER(c.nombre) like %?2% OR c.cedula like %?2%) and
				f.totaltarifa > 0
				and (( (f.estado = 1
					or f.estado = 2)
				and f.feccrea <= ?1
				and ( f.fechacobro >= ?1
					or f.fechacobro is null))
				or f.estado = 3 )
				and f.fechaconvenio is null
				and f.fechaeliminacion is null
			    AND rf.idrubro_rubros NOT IN (79, 5, 165)
			    AND (rf.estado = 1 or rf.estado is null)
				group by
			c.idcliente,
				c.nombre,
				c.cedula ,
				c.direccion ,
				c.email,
				c.telefono
			order by
				valor desc
			""", countQuery = """
				select
					count(*)
				from (
					select 1
					from clientes c
					join facturas f on c.idcliente = f.idcliente
					join rubroxfac rf on rf.idfactura_facturas = f.idfactura and (rf.estado = 1 or rf.estado is null)
					join rubros r on r.idrubro = rf.idrubro_rubros
					where
						(LOWER(c.nombre) like %?2% OR c.cedula like %?2%) and
						f.totaltarifa > 0
						and (( (f.estado = 1
							or f.estado = 2)
						and f.feccrea <= ?1
						and ( f.fechacobro >= ?1
							or f.fechacobro is null))
						or f.estado = 3 )
						and f.fechaconvenio is null
						and f.fechaeliminacion is null
						and (rf.estado = 1 or rf.estado is null)
					group by
					c.idcliente,
						c.nombre,
						c.cedula ,
						c.direccion ,
						c.email,
						c.telefono
				) as sub
			""", nativeQuery = true)
	Page<CVClientes> getCVOfNCliente(LocalDate fecha, String nombre, Pageable pageable);

	/* DUPLICADOS DE CLIENTES */
	@Query(value = """
			  SELECT
			    c.idcliente,
			    c.cedula,
			    c.nombre,
			    c.direccion,
			    c.telefono,
			    c.email,
			    c.activo,
			    COUNT(*) OVER (PARTITION BY c.cedula) AS repeticiones
			  FROM clientes c
			  WHERE c.activo = true
			    AND c.cedula IN (
			      SELECT c2.cedula
			      FROM clientes c2
			      WHERE c2.activo = true
			      GROUP BY c2.cedula
			      HAVING COUNT(*) > 1
			    )
			  ORDER BY c.cedula, c.idcliente
			""", countQuery = """
			  SELECT COUNT(*)
			  FROM clientes c
			  WHERE c.activo = true
			    AND c.cedula IN (
			      SELECT c2.cedula
			      FROM clientes c2
			      WHERE c2.activo = true
			      GROUP BY c2.cedula
			      HAVING COUNT(*) > 1
			    )
			""", nativeQuery = true)
	Page<ClienteDuplicadoView> findDuplicados(Pageable pageable);

	@Query(value = """
			  SELECT
			    c.cedula AS cedula,
			    CAST(COUNT(*) AS INTEGER) AS total,
			    STRING_AGG(c.nombre, ' | ' ORDER BY c.idcliente) AS nombres
			  FROM clientes c
			  WHERE c.activo = true
			    AND COALESCE(TRIM(c.cedula), '') <> ''
			  GROUP BY c.cedula
			  HAVING COUNT(*) > 1
			  ORDER BY c.cedula
			""", countQuery = """
			  SELECT COUNT(*)
			  FROM (
			    SELECT c.cedula
			    FROM clientes c
			    WHERE c.activo = true
			      AND COALESCE(TRIM(c.cedula), '') <> ''
			    GROUP BY c.cedula
			    HAVING COUNT(*) > 1
			  ) x
			""", nativeQuery = true)
	Page<ClienteDuplicadoGrupoView> findDuplicadosAgrupados(Pageable pageable);

	@Query(value = """
			  SELECT
			    c.cedula AS cedula,
			    CAST(COUNT(*) AS INTEGER) AS total,
			    STRING_AGG(c.nombre, ' | ' ORDER BY c.idcliente) AS nombres
			  FROM clientes c
			  WHERE c.activo = true
			    AND COALESCE(TRIM(c.cedula), '') <> ''
			    AND (
			      :q IS NULL OR :q = ''
			      OR c.cedula ILIKE CONCAT('%', :q, '%')
			      OR c.nombre ILIKE CONCAT('%', :q, '%')
			    )
			  GROUP BY c.cedula
			  HAVING COUNT(*) > 1
			  ORDER BY c.cedula
			""", countQuery = """
			  SELECT COUNT(*)
			  FROM (
			    SELECT c.cedula
			    FROM clientes c
			    WHERE c.activo = true
			      AND COALESCE(TRIM(c.cedula), '') <> ''
			      AND (
			        :q IS NULL OR :q = ''
			        OR c.cedula ILIKE CONCAT('%', :q, '%')
			        OR c.nombre ILIKE CONCAT('%', :q, '%')
			      )
			    GROUP BY c.cedula
			    HAVING COUNT(*) > 1
			  ) x
			""", nativeQuery = true)
	Page<ClienteDuplicadoGrupoView> findDuplicadosAgrupadosFiltrado(
			String q,
			Pageable pageable);
@Query(value = "SELECT c.idcliente AS idcliente, c.nombre AS nombre, c.cedula AS cedula, c.direccion AS direccion, c.telefono AS telefono, c.email AS email, c.activo AS activo FROM clientes c", nativeQuery = true)
	List<ClientesMobile> findAllBy();

}




