package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.ThAction;

public interface ThActionR extends JpaRepository<ThAction, Long> {

    @Query("""
            SELECT a
            FROM ThAction a
            WHERE a.idpersonal_personal.idpersonal = :idpersonal
            ORDER BY a.feccrea DESC
            """)
    List<ThAction> findByPersonal(@Param("idpersonal") Long idpersonal);
}
