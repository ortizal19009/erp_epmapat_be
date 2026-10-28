package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.SuspensionesM;

public interface SuspensionesR extends JpaRepository<SuspensionesM, Long>{
    // Todas las altas comparten el bloqueo porque los numeros y el ID se asignan mediante MAX + 1.
    @Query(value = "SELECT 1 FROM pg_advisory_xact_lock(7194361201)", nativeQuery = true)
    Integer bloquearNumeracion();

    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"iddocumento_documentos", "idsuspension_origen"})
    java.util.Optional<SuspensionesM> findById(Long id);

    @Override
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"iddocumento_documentos", "idsuspension_origen"})
    List<SuspensionesM> findAll();

    @Query("SELECT s FROM SuspensionesM s LEFT JOIN FETCH s.iddocumento_documentos LEFT JOIN FETCH s.idsuspension_origen WHERE s.tipo IN (2, 3) AND s.fecha BETWEEN ?1 AND ?2 ORDER BY s.numero DESC, s.idsuspension DESC")
	public List<SuspensionesM> findByFecha(Date desde, Date hasta);
    @Query("SELECT s FROM SuspensionesM s LEFT JOIN FETCH s.iddocumento_documentos LEFT JOIN FETCH s.idsuspension_origen WHERE s.tipo = 1 AND s.fecha BETWEEN ?1 AND ?2 ORDER BY s.numero DESC, s.idsuspension DESC")
	public List<SuspensionesM> findByFechaHabilitaciones(Date desde, Date hasta);
	@Query(value = "SELECT * FROM suspensiones s WHERE tipo IN (2, 3) ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
	public List<SuspensionesM> findLastTen();
	@Query(value = "SELECT * FROM suspensiones s WHERE numero = ?1", nativeQuery = true)
	public List<SuspensionesM> findByNumero(Long numero);
	@Query(value = "SELECT * FROM suspensiones s WHERE tipo = 1 ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
	public List<SuspensionesM> findHabilitaciones();

	@Query(value = "SELECT COALESCE(MAX(idsuspension), 0) + 1 FROM suspensiones", nativeQuery = true)
	Long findNextIdsuspension();

	@Query(value = "SELECT * FROM suspensiones s WHERE s.tipo = 1 ORDER BY s.numero DESC NULLS LAST, s.idsuspension DESC LIMIT 1", nativeQuery = true)
	SuspensionesM findUltimaHabilitacion();

	@Query(value = "SELECT * FROM suspensiones s WHERE s.tipo IN (2, 3) ORDER BY s.numero DESC NULLS LAST, s.idsuspension DESC LIMIT 1", nativeQuery = true)
	SuspensionesM findUltimaSuspension();
	
	SuspensionesM findFirstByOrderByIdsuspensionDesc();

}
