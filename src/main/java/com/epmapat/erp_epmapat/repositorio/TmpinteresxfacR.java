package com.epmapat.erp_epmapat.repositorio;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.epmapat.erp_epmapat.modelo.Tmpinteresxfac;

public interface TmpinteresxfacR extends JpaRepository<Tmpinteresxfac, Long> {
    Optional<Tmpinteresxfac> findByIdfactura(Long idfactura);
    List<Tmpinteresxfac> findAllByIdfacturaIn(Collection<Long> ids);}
