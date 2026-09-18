package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epmapat.erp_epmapat.modelo.RouteDeviationEvent;

public interface RouteDeviationEventR extends JpaRepository<RouteDeviationEvent, Long> {
    List<RouteDeviationEvent> findBySessionId(String sessionId);
}
