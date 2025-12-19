package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.ClienteMergeCliente;

public interface ClienteMergeClienteR extends JpaRepository<ClienteMergeCliente, Long> {
    // Obtener clientes duplicados absorbidos en un merge
    List<ClienteMergeCliente> findByIdMerge(Long idMerge);
}
