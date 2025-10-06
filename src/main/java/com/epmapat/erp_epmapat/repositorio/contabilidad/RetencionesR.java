package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;

public interface RetencionesR extends JpaRepository<Retenciones, Long> {

	// Retenciones por secuencial y fechas
	@Query(value = "SELECT * FROM retenciones WHERE secretencion1 BETWEEN (?1) AND (?2) and fechaemiret1 BETWEEN (?3) AND (?4) ORDER BY secretencion1 ASC", nativeQuery = true)
	public List<Retenciones> findDesdeHasta(String desdeSecu, String hastaSecu, Date desdeFecha, Date hastaFecha);

	@SuppressWarnings("null")
	@Query(value = "SELECT * FROM retenciones ORDER BY secretencion1", nativeQuery = true)
	public List<Retenciones> findAll();

	//Retencion(es) de un asiento
	@Query(value = "SELECT * FROM retenciones WHERE idasiento = (?1)", nativeQuery = true)
	public List<Retenciones> findByIdasiento(Long idasiento);

	// Última retención
	Retenciones findFirstByOrderBySecretencion1Desc();

	// Validar por Número
	@Query("SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END FROM Retenciones r WHERE r.secretencion1 = :secretencion1")
	boolean valSecretencion1( @Param("secretencion1") String secretencion1);

}
