package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.rrhh.modelo.RrhhEmployeeFileRecord;

public interface RrhhEmployeeFileRecordRepository extends JpaRepository<RrhhEmployeeFileRecord, UUID> {
    List<RrhhEmployeeFileRecord> findByEmployeeIdOrderByCreatedAtDesc(Long employeeId);
}
