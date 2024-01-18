package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.PrecioxCatM;

public interface PrecioxCatR extends JpaRepository<PrecioxCatM, Long> {

	@Query(value = "SELECT * FROM precioxcat LIMIT 10 ", nativeQuery = true)
	public List<PrecioxCatM> findAll();

	@Query(value = "SELECT * FROM precioxcat AS p WHERE p.idcategoria_categorias=?1  AND p.m3 BETWEEN ?2 and ?3 ORDER by m3", nativeQuery = true)
	public List<PrecioxCatM> findAll(Long idcategoria_categorias, Long dm3, Long hm3);

	@Query(value = "SELECT * FROM precioxcat AS p WHERE p.idcategoria_categorias=?1 AND p.m3=?2", nativeQuery = true)
	public List<PrecioxCatM> findConsumo(Long idcategoria, Long m3 );

}
