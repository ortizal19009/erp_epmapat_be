package com.epmapat.erp_epmapat.repositorio.contabilidad;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.contabilidad.Airxrete;

public interface AirxreteR extends JpaRepository<Airxrete, Long> {

 	// Airxrete de una Retención
	@Query(value = "select * from airxrete a where a.idrete = ?1", nativeQuery = true)
	public List<Airxrete> findByIdrete(Long idrete);

}
