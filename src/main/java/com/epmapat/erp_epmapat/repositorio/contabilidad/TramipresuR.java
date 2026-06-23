package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;

public interface TramipresuR extends JpaRepository<Tramipresu, Long> {

	// Busca trámites por números y fechas cargando beneficiario y documento.
	@EntityGraph(attributePaths = { "idbene", "intdoc" })
	@Query("SELECT t FROM Tramipresu t WHERE t.numero BETWEEN ?1 AND ?2 AND t.fecha BETWEEN ?3 AND ?4 ORDER BY t.numero ASC")
	List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, LocalDate desdeFecha, LocalDate hastaFecha);

	// Último trámite
	@EntityGraph(attributePaths = { "idbene", "intdoc" })
	Tramipresu findFirstByOrderByNumeroDesc();

	// Valida número de trámite
	boolean existsByNumero(Long numero);

	// Un trámite por número
	@EntityGraph(attributePaths = { "idbene", "intdoc" })
	Optional<Tramipresu> findByNumero(Long numero);

	@Override
	@EntityGraph(attributePaths = { "idbene", "intdoc" })
	Optional<Tramipresu> findById(Long id);
}
