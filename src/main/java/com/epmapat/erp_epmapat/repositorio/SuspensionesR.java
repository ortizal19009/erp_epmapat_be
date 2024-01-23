package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.SuspensionesM;

public interface SuspensionesR extends JpaRepository<SuspensionesM, Long>{
	@Query(value="SELECT * FROM suspensiones WHERE tipo = 1 AND fecha BETWEEN (?1) AND (?2)", nativeQuery = true)
	public List<SuspensionesM> findByFecha(Date desde, Date hasta);
	@Query(value="SELECT * FROM suspensiones WHERE tipo = 2 AND fecha BETWEEN (?1) AND (?2)", nativeQuery = true)
	public List<SuspensionesM> findByFechaHabilitaciones(Date desde, Date hasta);
	@Query(value = "SELECT * FROM suspensiones s WHERE tipo = 1 ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
	public List<SuspensionesM> findLastTen();
	@Query(value = "SELECT * FROM suspensiones s WHERE numero = ?1", nativeQuery = true)
	public List<SuspensionesM> findByNumero(Long numero);
	@Query(value = "SELECT * FROM suspensiones s WHERE tipo = 2 ORDER BY idsuspension DESC LIMIT 10", nativeQuery = true)
	public List<SuspensionesM> findHabilitaciones();
	
	SuspensionesM findFirstByOrderByIdsuspensionDesc();

}
