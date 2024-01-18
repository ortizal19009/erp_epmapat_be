package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Facxconvenio;

//@Repository

public interface FacxconvenioR extends JpaRepository<Facxconvenio, Long>{

   @Query(value = "SELECT * FROM facxconvenio LIMIT 10", nativeQuery = true)
	public List<Facxconvenio> find10();

   @Query(value = "SELECT * FROM facxconvenio WHERE idconvenio_convenios=?1", nativeQuery = true)
	List<Facxconvenio> findByConvenio(Long idconvenio);
   
}
