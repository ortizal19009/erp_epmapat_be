package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.DTO.BatchPointsRequestDto;
import com.epmapat.erp_epmapat.DTO.TrackingSessionDto;
import com.epmapat.erp_epmapat.modelo.TrackingSession;
import com.epmapat.erp_epmapat.servicio.TrackingServicio;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/tracking")
@CrossOrigin(origins = "*")
@Slf4j
public class TrackingController {

    @Autowired
    private TrackingServicio trackingServicio;

    @PostMapping("/start")
    public ResponseEntity<TrackingSession> startSession(@RequestBody TrackingSessionDto dto) {
        log.info("Iniciando sesión de tracking para usuario: {}", dto.getReaderId());
        return ResponseEntity.ok(trackingServicio.startSession(dto));
    }

    @PostMapping("/points/batch")
    public ResponseEntity<Map<String, Object>> addPointsBatch(@RequestBody BatchPointsRequestDto dto) {
        log.info("Recibido lote de {} puntos para sesión: {}", 
            dto.getPoints() != null ? dto.getPoints().size() : 0, 
            dto.getTrackingSessionId());
        trackingServicio.addPointsBatch(dto);
        return ResponseEntity.ok(Map.of(
                "status", "OK",
                "message", "Points added successfully",
                "points", dto.getPoints() == null ? 0 : dto.getPoints().size()));
    }

    @PostMapping("/finish")
    public ResponseEntity<TrackingSession> finishSession(@RequestBody TrackingSessionDto dto) {
        log.info("Finalizando sesión de tracking: {}", dto.getId());
        return ResponseEntity.ok(trackingServicio.finishSession(dto));
    }
}
