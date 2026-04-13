package com.epmapat.erp_epmapat.repositorio;

import com.epmapat.erp_epmapat.modelo.ClienteMergeFactura;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ClienteMergeFacturaR extends JpaRepository<ClienteMergeFactura, Long> {
    // Facturas afectadas por un merge
    @Query(value = "SELECT * FROM cliente_merge_facturas WHERE CAST(id_merge AS TEXT) = CAST(:idMerge AS TEXT)", nativeQuery = true)
    List<ClienteMergeFactura> findByIdMerge(@Param("idMerge") Long idMerge);

    // (Opcional) Buscar por factura específica
    List<ClienteMergeFactura> findByFacturaId(Long facturaId);
}
