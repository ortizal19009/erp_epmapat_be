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
   
  // @Query(value = "SELECT i.porcentaje FROM intereses i WHERE ((i.anio = ?1 AND i.mes >= ?3) OR (i.anio = ?2 AND i.mes BETWEEN ?3 AND ?4)) AND i.anio >= ?1", nativeQuery = true)
   @Query(value = "SELECT i.porcentaje from intereses i where i.anio = ?1 and i.mes between ?2 and ?3 ", nativeQuery = true)
   List<Float> porcentajes(int year, int monthValue, int monthValue2);
   
   
}
