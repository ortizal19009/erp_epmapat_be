package com.epmapat.erp_epmapat.repositorio;

import java.io.Serializable;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Convenios;

public interface ConveniosR extends JpaRepository<Convenios, Serializable> {

    @Query(value = "SELECT * FROM convenios order by nroconvenio DESC LIMIT 10", nativeQuery = true)
 	public List<Convenios> findAll();

 	@Query(value = "SELECT * FROM convenios AS c WHERE c.nroconvenio >= ?1 and c.nroconvenio <= ?2 ", nativeQuery = true)
 	public List<Convenios> findAll(Long desde, Long hasta);

 	//Busca por número de convenio (para validar)
 	@Query(value = "SELECT * FROM convenios AS c WHERE c.nroconvenio=?1", nativeQuery = true)
 	public List<Convenios> findNroconvenio(Long nroconvenio); 

 }
