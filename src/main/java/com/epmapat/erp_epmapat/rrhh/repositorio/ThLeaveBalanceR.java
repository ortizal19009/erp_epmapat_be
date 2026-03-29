package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;

public interface ThLeaveBalanceR extends JpaRepository<ThLeaveBalance, Long> {

    @Query("""
            SELECT b
            FROM ThLeaveBalance b
            WHERE b.idpersonal_personal.idpersonal = :idpersonal
            ORDER BY b.anio DESC
            """)
    List<ThLeaveBalance> findByPersonal(@Param("idpersonal") Long idpersonal);

    @Query("""
            SELECT b
            FROM ThLeaveBalance b
            WHERE b.idpersonal_personal.idpersonal = :idpersonal
              AND b.anio = :anio
            """)
    Optional<ThLeaveBalance> findByPersonalAndAnio(@Param("idpersonal") Long idpersonal,
                                                   @Param("anio") Integer anio);
}
