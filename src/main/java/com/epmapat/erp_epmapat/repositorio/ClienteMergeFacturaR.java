package com.epmapat.erp_epmapat.repositorio;

import com.epmapat.erp_epmapat.modelo.ClienteMergeFactura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteMergeFacturaR extends JpaRepository<ClienteMergeFactura, Long> {
    // Facturas afectadas por un merge
    List<ClienteMergeFactura> findByIdMerge(Long idMerge);

    // (Opcional) Buscar por factura específica
    List<ClienteMergeFactura> findByFacturaId(Long facturaId);
}
