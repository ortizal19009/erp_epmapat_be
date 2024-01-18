package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.PtoEmisionM;

// @Repository
public interface PtoEmisionR extends JpaRepository<PtoEmisionM, Long>{

	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM ptoemision AS p WHERE NOT EXISTS(SELECT * FROM cajas AS c WHERE c.idptoemision_ptoemision=p.idptoemision)AND p.idptoemision=?1 ", nativeQuery = true)
	void deleteByIdQ(Long id);

	@Query(value = "SELECT * FROM ptoemision AS p WHERE EXISTS(SELECT * FROM cajas AS c WHERE c.idptoemision_ptoemision=p.idptoemision)AND p.idptoemision=?1 ", nativeQuery = true)
	List<PtoEmisionM> used(Long id);
	
}
