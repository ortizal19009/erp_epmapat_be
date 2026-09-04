package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;

public interface CertipresuR extends JpaRepository<Certipresu, Long> {

	@Query("""
			SELECT c
			FROM Certipresu c
			LEFT JOIN FETCH c.intdoc
			LEFT JOIN FETCH c.idbene
			LEFT JOIN FETCH c.idbeneres
			WHERE c.tipo = :tipo
			  AND c.numero BETWEEN :desdeNum AND :hastaNum
			  AND c.fecha BETWEEN :desdeFecha AND :hastaFecha
			ORDER BY c.numero ASC
			""")
	List<Certipresu> findDesdeHasta(@Param("tipo") Integer tipo, @Param("desdeNum") Long desdeNum,
			@Param("hastaNum") Long hastaNum, @Param("desdeFecha") LocalDate desdeFecha,
			@Param("hastaFecha") LocalDate hastaFecha);

	// Ultima Certificación o Reintegrada
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Certipresu findFirstByTipoOrderByNumeroDesc(Integer tipo);

	// Busca Certificación o Reintegrada por número
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Certipresu findByNumeroAndTipo(Long numero, int tipo); // Si se usa

	@Override
	@EntityGraph(attributePaths = { "intdoc", "idbene", "idbeneres" })
	Optional<Certipresu> findById(Long idcerti);

	// Valida por Número
	boolean existsByNumeroAndTipo(Long numero, int tipo);

	// @Query("""
	// SELECT c.numero
	// FROM Certipresu c
	// WHERE c.tipo = 1
	// AND c.fecha <= :fecha
	// ORDER BY c.numero DESC
	// """)
	// Long findUltimoNumeroTipo1HastaFecha(@Param("fecha") LocalDate fecha);

	// BUsca la última certificacion hasta una fecha (para el navegador)
	@Query(value = """
			  SELECT c.numero
			  FROM certificaciones c
			  WHERE c.tipo = 1
			    AND c.fecha <= :fecha
			  ORDER BY c.numero DESC
			  LIMIT 1
			""", nativeQuery = true)
	Long findUltimoNumeroTipo1HastaFecha(@Param("fecha") LocalDate fecha);

}
