package com.epmapat.erp_epmapat.repositorio;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.Tmpinteresxfac;

public interface TmpinteresxfacR extends JpaRepository<Tmpinteresxfac, Long> {
    Tmpinteresxfac findByIdfactura(Long idfactura);
}
