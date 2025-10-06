package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.ResEmisiones;
import com.epmapat.erp_epmapat.modelo.Emisiones;

public interface EmisionesR extends JpaRepository<Emisiones, Long> {

	@Query(value = "select * from emisiones WHERE emision >= ?1 and emision <= ?2 ORDER BY emision desc", nativeQuery = true)
	List<Emisiones> findByDesdeHasta(String desde, String hasta);

	// Ultima Emisión
	Emisiones findFirstByOrderByEmisionDesc();

	@Query(value = "SELECT * FROM emisiones e join lecturas l on e.idemision = l.idemision join facturas f on l.idfactura = f.idfactura where not f.fechaeliminacion is null and l.idemision = ?1 order by f.idabonado", nativeQuery = true)
	List<Emisiones> findByIdEmisiones(Long idemision);

	@Query(value = """
						WITH resumen_rubros AS (
				SELECT
					rf.idfactura_facturas,
					SUM(rf.cantidad * rf.valorunitario) AS total_rubro
				FROM rubroxfac rf
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

}
