package com.epmapat.erp_epmapat.repositorio;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Facxrecauda;

public interface FacxrecaudaR extends JpaRepository<Facxrecauda, Long> {

	@Query(value = "select * from facxrecauda fr join recaudacion r on fr.idrecaudacion = r.idrecaudacion where r.recaudador = ?1 and (Date(r.fechacobro) BETWEEN ?2 AND ?3) order by r.fechacobro asc", nativeQuery = true)
	List<Facxrecauda> getByUsuFecha(Long idusuario, Date d, Date h);
	
	

}
