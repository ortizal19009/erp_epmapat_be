package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.administracion.Colores;

public interface ColoresR extends JpaRepository<Colores, Long>{

   @Query(value = "SELECT * FROM colores where LENGTH(codigo) = 2 order by idcolor", nativeQuery=true)
	List<Colores> findTonos();

   @Query(value = "SELECT * FROM colores where codigo like ?1% AND LENGTH(codigo) = 4 order by idcolor", nativeQuery=true)
	List<Colores> findByTono(String codigo);
   
}
