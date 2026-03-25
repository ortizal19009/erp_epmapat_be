package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.RecargosxcuentaAudit;

@Repository
public interface RecargosxcuentaAuditR extends JpaRepository<RecargosxcuentaAudit, Long> {
}
