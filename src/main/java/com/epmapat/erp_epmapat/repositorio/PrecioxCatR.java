package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.PrecioxCatM;

public interface PrecioxCatR extends JpaRepository<PrecioxCatM, Long> {

	@EntityGraph(attributePaths = {"idcategoria_categorias"})
	@Query("SELECT p FROM PrecioxCatM p LEFT JOIN FETCH p.idcategoria_categorias WHERE p.idprecioxcat = ?1")
	Optional<PrecioxCatM> findById(Long id);

	@EntityGraph(attributePaths = {"idcategoria_categorias"})
	@Query("SELECT p FROM PrecioxCatM p LEFT JOIN FETCH p.idcategoria_categorias ORDER BY p.idprecioxcat")
	public List<PrecioxCatM> findAll();

	@EntityGraph(attributePaths = {"idcategoria_categorias"})
	@Query("SELECT p FROM PrecioxCatM p LEFT JOIN FETCH p.idcategoria_categorias WHERE p.idcategoria_categorias.idcategoria = ?1 AND p.m3 BETWEEN ?2 AND ?3 ORDER BY p.m3")
	public List<PrecioxCatM> findAll(Long idcategoria_categorias, Long dm3, Long hm3);

	@EntityGraph(attributePaths = {"idcategoria_categorias"})
	@Query("SELECT p FROM PrecioxCatM p LEFT JOIN FETCH p.idcategoria_categorias WHERE p.idcategoria_categorias.idcategoria = ?1 AND p.m3 = ?2")
	public List<PrecioxCatM> findConsumo(Long idcategoria, Long m3 );

}
