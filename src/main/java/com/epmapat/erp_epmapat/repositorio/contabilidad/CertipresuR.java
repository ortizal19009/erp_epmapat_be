package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;

public interface CertipresuR extends JpaRepository<Certipresu, Long> {

	@Query(value = "SELECT * FROM certificaciones " +
			"WHERE tipo = ?1 " +
			"AND numero BETWEEN ?2 AND ?3 " +
			"AND fecha BETWEEN ?4 AND ?5 " +
			"ORDER BY numero ASC", nativeQuery = true)
	List<Certipresu> findDesdeHasta(Integer tipo, Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	// Ultima Certificación o Reintegrada
	Certipresu findFirstByTipoOrderByNumeroDesc(Integer tipo);

	// Busca Certificación o Reintegrada por número
	Certipresu findByNumeroAndTipo(Long numero, int tipo); // Si se usa

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
