package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeAction;

public interface RrhhEmployeeActionRepository extends JpaRepository<RrhhEmployeeAction, UUID> {
    List<RrhhEmployeeAction> findByEmployeeIdOrderByEffectiveDateDesc(Long employeeId);
}
