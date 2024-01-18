package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Intereses;

// @Repository
public interface InteresesR extends JpaRepository<Intereses, Long>{

   @Query(value = "SELECT * FROM intereses where anio =?1 and mes = ?2", nativeQuery=true)
	List<Intereses> findByAnioMes(Number anio, Number mes);

   @Query(value = "SELECT * FROM intereses order by anio DESC, mes DESC limit 1", nativeQuery=true)
	List<Intereses> findUltimo();
   
}
