package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhVacancy;

public interface RrhhVacancyRepository extends JpaRepository<RrhhVacancy, UUID>, JpaSpecificationExecutor<RrhhVacancy> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, UUID id);
}
