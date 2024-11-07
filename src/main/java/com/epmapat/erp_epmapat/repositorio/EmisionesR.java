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
	
	
	@Query(value = "SELECT * FROM emisiones e join lecturas l on e.idemision = l.idemision join facturas f on l.idfactura = f.idfactura where not f.fechaeliminacion is null and l.idemision = ?1 order by f.idabonado", nativeQuery = true)
	List<Emisiones> findByIdEmisiones(Long idemision); 

}
