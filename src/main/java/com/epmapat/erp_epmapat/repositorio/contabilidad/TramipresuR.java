package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;

public interface TramipresuR extends JpaRepository<Tramipresu, Long>{

	@Query(value = "SELECT * FROM tramipresu LIMIT 10", nativeQuery = true)
	public List<Tramipresu> findAll();
   
	@Query(value = "SELECT MAX(numero) FROM tramipresu t ORDER BY DESC", nativeQuery = true)
	public Long findMax();
	
	Tramipresu findFirstByOrderByNumeroDesc();
   
	@Query(value ="SELECT * FROM tramipresu WHERE numero BETWEEN (?1) AND (?2) and fecha BETWEEN (?3) AND (?4) ORDER BY numero ASC", nativeQuery = true)
	public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);

}
