package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;

public interface TramipresuR extends JpaRepository<Tramipresu, Long> {

	// Busca Trámites por numeros y fechas
	@Query(value = "SELECT * FROM tramites WHERE numero BETWEEN (?1) AND (?2) and fecha BETWEEN (?3) AND (?4) ORDER BY numero ASC", nativeQuery = true)
	public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

	// Último Trámite
	Tramipresu findFirstByOrderByNumeroDesc();

	// Valida Número de Tramite
	boolean existsByNumero(Long numero);

	// Un Trámite por número
	Optional<Tramipresu> findByNumero(Long numero);

}
