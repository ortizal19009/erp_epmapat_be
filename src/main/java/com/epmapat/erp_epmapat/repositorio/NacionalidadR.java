package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
//import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.Nacionalidad;


public interface NacionalidadR extends JpaRepository<Nacionalidad, Long>{

	@Query(value = "SELECT * FROM nacionalidad LIMIT 10", nativeQuery=true)
	List<Nacionalidad> findAll();
	
	@Transactional
	@Modifying(clearAutomatically = true)
	@Query(value = "DELETE FROM nacionalidad AS n WHERE NOT EXISTS(SELECT * FROM clientes AS c WHERE c.idnacionalidad_nacionalidad=n.idnacionalidad)AND n.idnacionalidad=?1 ", nativeQuery = true)
	void deleteByIdQ(Long id);
	
	@Query(value = "SELECT * FROM nacionalidad AS n WHERE EXISTS(SELECT * FROM clientes AS c WHERE c.idnacionalidad_nacionalidad=n.idnacionalidad)AND n.idnacionalidad=?1 ", nativeQuery = true)
	List<Nacionalidad> used(Long id);
	
	@Query(value="SELECT * FROM nacionalidad AS n WHERE LOWER(n.descripcion)=?1",nativeQuery = true)
	List<Nacionalidad> findByDescription(String descripcion);

}