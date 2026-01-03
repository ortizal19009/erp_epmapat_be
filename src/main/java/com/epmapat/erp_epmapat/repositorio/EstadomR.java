package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
//import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.interfaces.mobile.EstadomMobile;
import com.epmapat.erp_epmapat.modelo.Estadom;

//@Repository
public interface EstadomR extends JpaRepository<Estadom, Long> {

    
	/*
	 * =============================================================
	 * QUERYS PARA MOBILE
	 * =============================================================
	 */
    @Query(value = "SELECT e.idestadom AS idestadom, e.descripcion AS descripcion FROM estadom e", nativeQuery = true)
    List<EstadomMobile> findAllEstadom();
    
}
