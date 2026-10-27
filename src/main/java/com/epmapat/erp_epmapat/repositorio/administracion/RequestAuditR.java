package com.epmapat.erp_epmapat.repositorio.administracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import com.epmapat.erp_epmapat.modelo.administracion.RequestAudit;
public interface RequestAuditR extends JpaRepository<RequestAudit, Long>, JpaSpecificationExecutor<RequestAudit> {}
