package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
// import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Servicios1M;

// @Repository
public interface Servicios1R extends JpaRepository<Servicios1M, Long>{
	@Query(value="SELECT * FROM servicios1 AS s WHERE idmodulo_modulos=?1",nativeQuery = true)
	public List<Servicios1M> findByIdModulos(Long idmodulo);
}
