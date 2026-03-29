package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhIncentive;

public interface RrhhIncentiveRepository extends JpaRepository<RrhhIncentive, UUID>, JpaSpecificationExecutor<RrhhIncentive> {
}
