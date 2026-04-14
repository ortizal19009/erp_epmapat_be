package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;

public interface PartixcertiR extends JpaRepository<Partixcerti, Long> {

	// Partidas de una certipresu ordenadas por codpar
	List<Partixcerti> findByIdcerti_IdcertiOrderByIntpre_Codpar(Long idcerti);

	// Cuenta las partidas de una certipresu
	short countByIdcerti_Idcerti(Long idcerti);

	// Suma partixcerti.valor para totalizar en certipresu.valor
	@Query("SELECT COALESCE(SUM(p.valor), 0) FROM Partixcerti p WHERE p.idcerti.idcerti = :idcerti")
	BigDecimal sumaValoresPorCertificacion(Long idcerti);

	// Suma partixcerti.valor para totalizar en presupue.totcerti
	@Query("""
			    SELECT COALESCE( SUM( CASE WHEN p.idparxcer_ IS NULL THEN p.valor ELSE -p.valor END ), 0)
			    FROM Partixcerti p
			    WHERE p.intpre.intpre = :intpre
			""")
	BigDecimal sumaValoresPorPartida(Long intpre);

	// Actualiza swreinte de las originales
	@Modifying
	@Query("UPDATE Partixcerti p SET p.swreinte = :valor WHERE p.idparxcer = :id")
	void actualizaSwreinte(@Param("id") Long id, @Param("valor") short valor);

	// Partixcerti de un intpre (Certificaciones de una Partida)
	@Query("SELECT p FROM Partixcerti p JOIN p.idcerti c WHERE p.intpre.intpre = :intpre AND c.fecha BETWEEN :desde AND :hasta ORDER BY c.fecha, c.numero ASC ")
	List<Partixcerti> findByIntpreDesdeHasta(Long intpre, LocalDate desde, LocalDate hasta);

}