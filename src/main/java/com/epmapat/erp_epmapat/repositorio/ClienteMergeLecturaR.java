package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.modelo.ClienteMergeLectura;

public interface ClienteMergeLecturaR extends JpaRepository<ClienteMergeLectura, Long> {
    @Query(value = "SELECT * FROM cliente_merge_lecturas WHERE CAST(id_merge AS TEXT) = CAST(:idMerge AS TEXT)", nativeQuery = true)
    List<ClienteMergeLectura> findByIdMerge(@Param("idMerge") Long idMerge);
}
