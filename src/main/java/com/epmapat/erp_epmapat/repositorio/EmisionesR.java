package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Emisiones;

public interface EmisionesR extends JpaRepository<Emisiones, Long> {

   @Query(value = "select * from emisiones WHERE emision >= ?1 and emision <= ?2 ORDER BY emision desc", nativeQuery = true )
	List<Emisiones> findByDesdeHasta(String desde, String hasta);

   //Ultima Emisión
	Emisiones findFirstByOrderByEmisionDesc();

}
