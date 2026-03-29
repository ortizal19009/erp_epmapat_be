package com.epmapat.erp_epmapat.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.rrhh.modelo.Cargos;

@org.springframework.stereotype.Repository("rrhhLegacyCargosR")
public interface CargosR extends JpaRepository<Cargos, Long> {
    
}

