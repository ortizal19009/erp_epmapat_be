package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.interfaces.EmisionesInterface;
import com.epmapat.erp_epmapat.interfaces.ResEmisiones;
import com.epmapat.erp_epmapat.modelo.Emisiones;

public interface EmisionesR extends JpaRepository<Emisiones, Long> {

	@Query(value = "select * from emisiones WHERE emision >= ?1 and emision <= ?2 ORDER BY emision desc", nativeQuery = true)
	List<Emisiones> findByDesdeHasta(String desde, String hasta);

	// Ultima Emisión
	Emisiones findFirstByOrderByEmisionDesc();

	Emisiones findFirstByEstadoOrderByEmisionDesc(Integer estado);

	@Query(value = "SELECT * FROM emisiones e join lecturas l on e.idemision = l.idemision join facturas f on l.idfactura = f.idfactura where not f.fechaeliminacion is null and l.idemision = ?1 order by f.idabonado", nativeQuery = true)
	List<Emisiones> findByIdEmisiones(Long idemision);

	@Query(value = """
						WITH resumen_rubros AS (
				SELECT
					rf.idfactura_facturas,
					SUM(rf.cantidad * rf.valorunitario) AS total_rubro
				FROM rubroxfac rf
				WHERE (rf.estado <> 0 OR rf.estado IS NULL)
				GROUP BY rf.idfactura_facturas
			)

			SELECT
				e.idemision,
				e.emision,
				SUM(rr.total_rubro) AS valemision,
				SUM(l.lecturaactual - l.lecturaanterior) AS m3,
				COUNT(l.idlectura) AS ncuentas,

				SUM(
					CASE
						WHEN f.totaltarifa > 0 AND f.pagado = 1 AND (f.estado = 2 OR f.estado = 1)
						THEN rr.total_rubro
						ELSE 0
					END
				) AS total_pagado,

				SUM(
					CASE
						WHEN (f.totaltarifa > 0 AND f.pagado = 0) OR (f.estado = 3 AND f.pagado = 1)
						THEN rr.total_rubro
						ELSE 0
					END
				) AS total_pendiente

			FROM lecturas l
			JOIN emisiones e ON e.idemision = l.idemision
			JOIN facturas f ON l.idfactura = f.idfactura
			JOIN resumen_rubros rr ON rr.idfactura_facturas = f.idfactura

			WHERE f.fechaeliminacion IS null and e.estado = 1

			GROUP BY e.idemision, e.emision
			ORDER BY e.idemision DESC
			LIMIT ?1
						""", nativeQuery = true)
	public List<ResEmisiones> ResumenEmisiones(Long limit);

	@Query(value = """
			SELECT
			    f.idabonado                                  AS cuenta,
			    f.idfactura                                  AS idfactura,
			    (l.lecturaactual - l.lecturaanterior)        AS m3,
			    a.idcategoria                                AS categoria,
			    COALESCE(a.sw_municipio,    false)           AS swMunicipio,
			    COALESCE(a.sw_adultomayor,  false)           AS swAdultoMayor,
			    COALESCE(a.sw_aguapotable,  false)           AS swAguapotable
			FROM lecturas l
			JOIN facturas  f ON f.idfactura = l.idfactura
			JOIN emisiones e ON e.idemision = l.idemision
			LEFT JOIN abonados a ON a.idabonado = f.idabonado
			WHERE e.idemision = ?1
			  AND f.fechaeliminacion IS NULL
			ORDER BY f.idabonado
			""", nativeQuery = true)
	List<EmisionesInterface> getSwAguapotable(Long idemision);

	@Query("""
			select e from Emisiones e
			where e.emision > :emision
			  and e.estado in :estados
			order by e.emision asc
			""")
	List<Emisiones> findPosterioresByEmisionAndEstadoIn(@Param("emision") String emision,
			@Param("estados") List<Integer> estados);

}




