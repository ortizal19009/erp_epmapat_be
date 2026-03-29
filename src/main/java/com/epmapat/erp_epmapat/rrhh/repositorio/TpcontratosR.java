package com.epmapat.erp_epmapat.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.rrhh.modelo.Tpcontratos;

@org.springframework.stereotype.Repository("rrhhLegacyTpcontratosR")
public interface TpcontratosR extends JpaRepository<Tpcontratos, Long>{
    
}

