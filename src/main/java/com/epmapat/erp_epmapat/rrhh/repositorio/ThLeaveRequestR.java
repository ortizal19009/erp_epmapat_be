package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest;

public interface ThLeaveRequestR extends JpaRepository<ThLeaveRequest, Long> {

    @Query("""
            SELECT r
            FROM ThLeaveRequest r
            WHERE r.idpersonal_personal.idpersonal = :idpersonal
            ORDER BY r.feccrea DESC
            """)
    List<ThLeaveRequest> findByPersonal(@Param("idpersonal") Long idpersonal);

    @Query("""
            SELECT r
            FROM ThLeaveRequest r
            WHERE UPPER(r.estado) = UPPER(:estado)
            ORDER BY r.feccrea DESC
            """)
    List<ThLeaveRequest> findByEstado(@Param("estado") String estado);

    @Query("""
            SELECT (count(r) > 0)
            FROM ThLeaveRequest r
            WHERE r.idpersonal_personal.idpersonal = :idpersonal
              AND r.estado IN ('SOLICITADA', 'APROBADA')
              AND :inicio <= r.fechafin
              AND :fin >= r.fechainicio
            """)
    boolean existsSolape(@Param("idpersonal") Long idpersonal,
                         @Param("inicio") LocalDate inicio,
                         @Param("fin") LocalDate fin);
}
