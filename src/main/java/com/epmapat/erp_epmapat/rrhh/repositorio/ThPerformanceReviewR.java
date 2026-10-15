package com.epmapat.erp_epmapat.rrhh.repositorio;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epmapat.erp_epmapat.rrhh.modelo.ThPerformanceReview;

public interface ThPerformanceReviewR extends JpaRepository<ThPerformanceReview, Long> {
    Page<ThPerformanceReview> findByIdpersonalOrderByFechaDescIdreviewDesc(Long idpersonal, Pageable pageable);
}
