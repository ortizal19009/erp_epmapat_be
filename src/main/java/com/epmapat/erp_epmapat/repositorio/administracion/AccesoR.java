package com.epmapat.erp_epmapat.repositorio.administracion;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.administracion.Acceso;

public interface AccesoR extends JpaRepository<Acceso, Long>{
   
   @Query(value = "SELECT * FROM acceso order by codacc", nativeQuery = true)
   List<Acceso> findAll();
   
}
