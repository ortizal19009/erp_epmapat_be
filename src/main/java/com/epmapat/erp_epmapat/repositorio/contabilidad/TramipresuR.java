package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.contabilidad.Tramipresu;

public interface TramipresuR extends JpaRepository<Tramipresu, Long>{

	@Query(value ="SELECT * FROM tramites WHERE numero BETWEEN (?1) AND (?2) and fecha BETWEEN (?3) AND (?4) ORDER BY numero ASC", nativeQuery = true)
	public List<Tramipresu> findDesdeHasta(Long desdeNum, Long hastaNum, Date desdeFecha, Date hastaFecha);
	
	Tramipresu findFirstByOrderByNumeroDesc();
   
	// Valida Número de Tramite
	@Query("SELECT COUNT(b) > 0 FROM Tramipresu b WHERE b.numero = :numero")
	boolean valNumero(@Param("numero") Long numero);


}
