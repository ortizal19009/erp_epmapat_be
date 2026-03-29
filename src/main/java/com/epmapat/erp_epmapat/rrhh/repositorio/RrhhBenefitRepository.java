package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhBenefit;

public interface RrhhBenefitRepository extends JpaRepository<RrhhBenefit, UUID>, JpaSpecificationExecutor<RrhhBenefit> {
}
