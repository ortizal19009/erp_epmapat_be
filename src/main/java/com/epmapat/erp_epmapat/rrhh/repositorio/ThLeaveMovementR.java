package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement.Tipo;

public interface ThLeaveMovementR extends JpaRepository<ThLeaveMovement, Long> {
    @org.springframework.data.jpa.repository.EntityGraph(attributePaths = {"balance", "origen"})
    List<ThLeaveMovement> findByRequestIdrequestOrderByIdmovementAsc(Long requestId);
    Optional<ThLeaveMovement> findByBalanceIdbalanceAndClave(Long balanceId, String clave);
    boolean existsByBalanceIdbalanceAndTipo(Long balanceId, Tipo tipo);
    Optional<ThLeaveMovement> findByRequestIdrequestAndTipo(Long requestId, Tipo tipo);
    Optional<ThLeaveMovement> findFirstByBalanceIdbalanceOrderByIdmovementDesc(Long balanceId);

    @Query("SELECT m FROM ThLeaveMovement m JOIN FETCH m.balance b LEFT JOIN FETCH m.request LEFT JOIN FETCH m.origen "
            + "WHERE b.idpersonal_personal.idpersonal = :personal AND (:anio IS NULL OR b.anio = :anio) "
            + "ORDER BY m.idmovement DESC")
    List<ThLeaveMovement> findByPersonal(@Param("personal") Long personal, @Param("anio") Integer anio);
}
