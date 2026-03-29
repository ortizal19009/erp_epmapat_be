package com.epmapat.erp_epmapat.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.rrhh.modelo.Detcargo;

@org.springframework.stereotype.Repository("rrhhLegacyDetcargoR")
public interface DetcargoR extends JpaRepository<Detcargo, Long> {

}

