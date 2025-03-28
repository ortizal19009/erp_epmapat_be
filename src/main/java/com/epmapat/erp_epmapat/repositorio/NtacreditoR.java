package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Ntacredito;

public interface NtacreditoR extends JpaRepository<Ntacredito, Long> {
@Query(value = "SELECT * FROM ntacredito", nativeQuery = true)
public Page<Ntacredito> findAllNtaCreditos(Pageable pageable);
}