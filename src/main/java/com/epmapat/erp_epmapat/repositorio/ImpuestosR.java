package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.epmapat.erp_epmapat.modelo.Impuestos;

public interface ImpuestosR extends JpaRepository<Impuestos, Long> {
    @Query(value = "SELECT * FROM impuestos where estado = true", nativeQuery = true)
    Impuestos getCurrentImpuesto();
}
