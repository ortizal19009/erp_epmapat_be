package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.AuditoriaGenerica;

@Repository
public interface AuditoriaGenericaR extends JpaRepository<AuditoriaGenerica, Long> {
    List<AuditoriaGenerica> findByEntidadInOrderByFecmodiDesc(List<String> entidades);

    List<AuditoriaGenerica> findByEntidadInAndEntidadIdOrderByFecmodiDesc(
            List<String> entidades,
            Long entidadId);
}
