package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Partixcerti;

public interface PartixcertiR extends JpaRepository<Partixcerti, Long> {

	// Partidas de una certipresu
	List<Partixcerti> findByIdcerti_Idcerti(Long idcerti);

	// Cuenta las partidas de una certipresu
	short countByIdcerti_Idcerti(Long idcerti);

	// Suma partixcerti.valor para totalizar en certipresu.valor
	@Query("SELECT COALESCE(SUM(p.valor), 0) FROM Partixcerti p WHERE p.idcerti.idcerti = :idcerti")
	BigDecimal sumarValoresPorCertificacion(Long idcerti);

	// @Query("SELECT COALESCE(SUM(p.valor), 0) FROM Partixcerti p WHERE
	// p.intpre.intpre = :intpre")
	// BigDecimal sumaValoresPorPartida(Long intpre);

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

}