package com.epmapat.erp_epmapat.rrhh.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployee;

public interface RrhhEmployeeRepository extends JpaRepository<RrhhEmployee, Long>, JpaSpecificationExecutor<RrhhEmployee> {
    boolean existsByCode(String code);
    boolean existsByCodeAndIdNot(String code, Long id);
    boolean existsByIdentification(String identification);
    boolean existsByIdentificationAndIdNot(String identification, Long id);
    boolean existsByEmail(String email);
    boolean existsByEmailAndIdNot(String email, Long id);
}
