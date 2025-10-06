package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Modulos;

public interface ModulosR extends JpaRepository<Modulos, Long>{
	
	List<Modulos> findByOrderByDescripcionAsc();

	@Query(value="SELECT * FROM modulos WHERE idmodulo = 23 OR idmodulo = 50", nativeQuery = true)
	public List<Modulos> getTwoModulos();

}
