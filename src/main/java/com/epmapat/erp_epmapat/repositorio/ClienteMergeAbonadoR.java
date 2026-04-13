package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.ClienteMergeAbonado;

public interface ClienteMergeAbonadoR extends JpaRepository<ClienteMergeAbonado, Long> {
    @Query(value = "SELECT * FROM cliente_merge_abonados WHERE CAST(id_merge AS TEXT) = CAST(:idMerge AS TEXT)", nativeQuery = true)
    List<ClienteMergeAbonado> findByIdMerge(@Param("idMerge") Long idMerge);
}
