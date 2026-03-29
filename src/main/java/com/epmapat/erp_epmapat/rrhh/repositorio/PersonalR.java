package com.epmapat.erp_epmapat.rrhh.repositorio;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.epmapat.erp_epmapat.rrhh.modelo.Personal;

@org.springframework.stereotype.Repository("rrhhLegacyPersonalR")
public interface PersonalR extends JpaRepository<Personal, Long> {
    boolean existsByIdentificacion(String identificacion);

    boolean existsByIdentificacionAndIdpersonalNot(String identificacion, Long idpersonal);

    Optional<Personal> findByIdpersonal(Long idpersonal);

    @Query("""
            SELECT p FROM Personal p
            WHERE (:q IS NULL OR :q = ''
               OR LOWER(p.nombres) LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(p.apellidos) LIKE LOWER(CONCAT('%', :q, '%'))
               OR p.identificacion LIKE CONCAT('%', :q, '%'))
              AND (:estado IS NULL OR p.estado = :estado)
            ORDER BY p.idpersonal DESC
            """)
    Page<Personal> search(@Param("q") String q, @Param("estado") Boolean estado, Pageable pageable);
}
