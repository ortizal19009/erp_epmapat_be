package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.AuditoriaGenerica;

@Repository
public interface AuditoriaGenericaR extends JpaRepository<AuditoriaGenerica, Long> {
}
