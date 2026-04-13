package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.ClienteMergeCliente;

public interface ClienteMergeClienteR extends JpaRepository<ClienteMergeCliente, Long> {
    // Obtener clientes duplicados absorbidos en un merge
    @Query(value = "SELECT * FROM cliente_merge_clientes WHERE CAST(id_merge AS TEXT) = CAST(:idMerge AS TEXT)", nativeQuery = true)
    List<ClienteMergeCliente> findByIdMerge(@Param("idMerge") Long idMerge);
}
