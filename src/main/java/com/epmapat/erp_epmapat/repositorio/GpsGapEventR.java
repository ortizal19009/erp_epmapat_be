package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.epmapat.erp_epmapat.modelo.GpsGapEvent;

public interface GpsGapEventR extends JpaRepository<GpsGapEvent, Long> {
    List<GpsGapEvent> findBySessionId(String sessionId);
}
