package com.epmapat.erp_epmapat.repositorio;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.epmapat.erp_epmapat.modelo.TrackingPoint;

@Repository
public interface TrackingPointR extends JpaRepository<TrackingPoint, Long> {
    List<TrackingPoint> findBySessionId(String sessionId);
    
    @org.springframework.data.jpa.repository.Query("SELECT p FROM TrackingPoint p WHERE p.session.id = :sessionId ORDER BY p.capturedAt ASC")
    List<TrackingPoint> findBySessionIdOrderByCapturedAtAsc(String sessionId);

    boolean existsByClientPointId(String clientPointId);
}
