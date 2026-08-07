package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.FacturaReasignacionHistorial;

public interface FacturaReasignacionHistorialR extends JpaRepository<FacturaReasignacionHistorial, Long> {
    List<FacturaReasignacionHistorial> findByIdfacturaOrderByFechareasignacionDesc(Long idfactura);
}
