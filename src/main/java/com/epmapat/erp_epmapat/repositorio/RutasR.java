package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.CuentasByRutas;
import com.epmapat.erp_epmapat.interfaces.RutaAsignacionResumen;
import com.epmapat.erp_epmapat.interfaces.RutaResumen;
import com.epmapat.erp_epmapat.modelo.Rutas;

// @Repository
public interface RutasR extends JpaRepository<Rutas, Long> {

	// @Query(value = "SELECT * FROM rutas where codigo=?1", nativeQuery=true)
	// List<Rutas> findByCodigo(String codigo);

	// Valida Código de la Ruta
	@Query("SELECT COUNT(r) > 0 FROM Rutas r WHERE r.codigo = :codigo")
	boolean valCodigo(@Param("codigo") String codigo);

	@Query(value = "SELECT * FROM rutas WHERE estado = true ORDER BY idruta asc", nativeQuery = true)
	List<Rutas> findAllActive();

	@Query(value = """
		SELECT
			r.idruta AS idruta,
			r.descripcion AS descripcion,
			r.codigo AS codigo,
			r.estado AS estado
		FROM rutas r
		WHERE (:estado IS NULL OR r.estado = :estado)
		ORDER BY r.descripcion ASC
		""", nativeQuery = true)
	List<RutaResumen> findResumenByEstado(@Param("estado") Boolean estado);

	@Query(value = """
		SELECT
			r.idruta AS idruta,
			r.descripcion AS descripcion,
			r.codigo AS codigo,
			r.estado AS estado,
			CASE WHEN ocupadas.idruta IS NOT NULL THEN true ELSE false END AS ocupada
		FROM rutas r
		LEFT JOIN (
			SELECT DISTINCT CAST(rj->>'idruta' AS bigint) AS idruta
			FROM usrxrutas u
			CROSS JOIN LATERAL jsonb_array_elements(u.rutas) rj
			WHERE (:idemision IS NOT NULL AND u.idemision_emisiones = :idemision)
		) ocupadas ON ocupadas.idruta = r.idruta
		WHERE (:estado IS NULL OR r.estado = :estado)
		  AND (
			:filtro IS NULL
			OR TRIM(:filtro) = ''
			OR LOWER(r.descripcion) LIKE LOWER(CONCAT('%', :filtro, '%'))
			OR LOWER(COALESCE(r.codigo, '')) LIKE LOWER(CONCAT('%', :filtro, '%'))
			OR CAST(r.idruta AS text) LIKE CONCAT('%', :filtro, '%')
		  )
		ORDER BY r.descripcion ASC
		LIMIT :limit
		""", nativeQuery = true)
	List<RutaAsignacionResumen> findRutasAsignacion(
		@Param("idemision") Long idemision,
		@Param("estado") Boolean estado,
		@Param("filtro") String filtro,
		@Param("limit") Integer limit);

	@Query(value = """
				select
				r.idruta,
				r.descripcion,
				r.codigo ,
				count(a) as ncuentas
			from
				rutas r
			join abonados a on
				a.idruta_rutas = r.idruta
			group by
				r.idruta
			order by
				r.idruta asc
			""", nativeQuery = true)
	public List<CuentasByRutas> getNcuentasByRutas();
}
