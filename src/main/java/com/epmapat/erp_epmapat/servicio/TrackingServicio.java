package com.epmapat.erp_epmapat.servicio;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.DTO.BatchPointsRequestDto;
import com.epmapat.erp_epmapat.DTO.GpsGapEventDto;
import com.epmapat.erp_epmapat.DTO.RouteDeviationEventDto;
import com.epmapat.erp_epmapat.DTO.TrackingPointDto;
import com.epmapat.erp_epmapat.DTO.TrackingSessionDto;
import com.epmapat.erp_epmapat.modelo.GpsGapEvent;
import com.epmapat.erp_epmapat.modelo.RouteDeviationEvent;
import com.epmapat.erp_epmapat.modelo.TrackingPoint;
import com.epmapat.erp_epmapat.modelo.TrackingSession;
import com.epmapat.erp_epmapat.repositorio.GpsGapEventR;
import com.epmapat.erp_epmapat.repositorio.RouteDeviationEventR;
import com.epmapat.erp_epmapat.repositorio.TrackingPointR;
import com.epmapat.erp_epmapat.repositorio.TrackingSessionR;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class TrackingServicio {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TrackingSessionR trackingSessionR;

    @Autowired
    private TrackingPointR trackingPointR;

    @Autowired
    private RouteDeviationEventR routeDeviationEventR;

    @Autowired
    private GpsGapEventR gpsGapEventR;

    @Autowired
    private UsuariosR usuariosR;

    @Transactional
    public TrackingSession startSession(TrackingSessionDto dto) {
        TrackingSession existente = trackingSessionR.findById(dto.getId()).orElse(null);
        if (existente != null) {
            if (!existente.getReaderId().equals(dto.getReaderId())) {
                throw new IllegalArgumentException("La sesión pertenece a otro lector");
            }
            return existente;
        }

        TrackingSession session = TrackingSession.builder()
                .id(dto.getId())
                .readerId(dto.getReaderId())
                .routeId(dto.getRouteId())
                .deviceId(dto.getDeviceId())
                .workDate(dto.getWorkDate())
                .startTime(dto.getStartTime())
                .startBatteryLevel(dto.getStartBatteryLevel())
                .status("STARTED")
                .totalDistanceMeters(0.0)
                .totalPoints(0)
                .totalReadings(0)
                .build();
        return trackingSessionR.save(session);
    }

    @Transactional
    public void addPointsBatch(BatchPointsRequestDto dto) {
        try {
            log.info("DTO recibido: {}", objectMapper.writeValueAsString(dto));
        } catch (Exception e) {
            log.warn("No se pudo loguear el DTO: {}", e.getMessage());
        }

        log.info("Procesando lote de puntos para sesión: {}. Puntos recibidos: {}", 
            dto.getTrackingSessionId(), 
            dto.getPoints() != null ? dto.getPoints().size() : 0);

        TrackingSession session = trackingSessionR.findById(dto.getTrackingSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found: " + dto.getTrackingSessionId()));

        if (dto.getPoints() == null || dto.getPoints().isEmpty()) {
            log.warn("El lote de puntos está vacío o es nulo");
        } else {
            List<TrackingPoint> points = dto.getPoints().stream()
                .filter(pDto -> {
                    if (pDto.getClientPointId() != null && trackingPointR.existsByClientPointId(pDto.getClientPointId())) {
                        log.debug("Punto duplicado omitido: {}", pDto.getClientPointId());
                        return false;
                    }
                    return true;
                })
                .map(pDto -> 
                TrackingPoint.builder()
                    .clientPointId(pDto.getClientPointId())
                    .session(session)
                    .latitude(pDto.getLatitude())
                    .longitude(pDto.getLongitude())
                    .accuracy(pDto.getAccuracy())
                    .altitude(pDto.getAltitude())
                    .speed(pDto.getSpeed())
                    .bearing(pDto.getBearing())
                    .batteryLevel(pDto.getBatteryLevel())
                    .isMockLocation(pDto.getIsMockLocation())
                    .routeStatus(pDto.getRouteStatus())
                    .capturedAt(pDto.getCapturedAt())
                    .build()
            ).collect(Collectors.toList());

            if (!points.isEmpty()) {
                trackingPointR.saveAll(points);
                log.info("Se guardaron {} puntos nuevos en la base de datos", points.size());
            } else {
                log.warn("Todos los puntos del lote eran duplicados y fueron omitidos");
            }
            
            // Update session stats
            session.setTotalPoints(session.getTotalPoints() + points.size());
            trackingSessionR.save(session);
        }

        // Eventos (Desviaciones y Huecos)
        if (dto.getDeviationEvents() != null && !dto.getDeviationEvents().isEmpty()) {
            log.info("Guardando {} eventos de desviación", dto.getDeviationEvents().size());
            List<RouteDeviationEvent> deviations = dto.getDeviationEvents().stream().map(dDto ->
                RouteDeviationEvent.builder()
                    .session(session)
                    .readerId(dDto.getReaderId())
                    .exitTime(toLocalDateTime(dDto.getExitTime()))
                    .returnTime(toLocalDateTime(dDto.getReturnTime()))
                    .exitLatitude(dDto.getExitLatitude())
                    .exitLongitude(dDto.getExitLongitude())
                    .maximumDistanceMeters(dDto.getMaximumDistanceMeters())
                    .durationSeconds(dDto.getDurationSeconds())
                    .build()
            ).collect(Collectors.toList());
            routeDeviationEventR.saveAll(deviations);
        }

        if (dto.getGapEvents() != null && !dto.getGapEvents().isEmpty()) {
            log.info("Guardando {} eventos de huecos GPS", dto.getGapEvents().size());
            List<GpsGapEvent> gaps = dto.getGapEvents().stream().map(gDto ->
                GpsGapEvent.builder()
                    .session(session)
                    .startTime(toLocalDateTime(gDto.getStartTime()))
                    .endTime(toLocalDateTime(gDto.getEndTime()))
                    .durationSeconds(gDto.getDurationSeconds())
                    .lastLatitude(gDto.getLastLatitude())
                    .lastLongitude(gDto.getLastLongitude())
                    .build()
            ).collect(Collectors.toList());
            gpsGapEventR.saveAll(gaps);
        }
    }

    private LocalDateTime toLocalDateTime(Long timestamp) {
        if (timestamp == null || timestamp == 0) return null;
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(timestamp), ZoneId.systemDefault());
    }

    @Transactional
    public TrackingSession finishSession(TrackingSessionDto dto) {
        TrackingSession session = trackingSessionR.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Session not found: " + dto.getId()));

        session.setEndTime(dto.getEndTime());
        session.setEndBatteryLevel(dto.getEndBatteryLevel());
        session.setStatus("FINISHED");
        if (dto.getTotalDistanceMeters() != null) {
            session.setTotalDistanceMeters(dto.getTotalDistanceMeters());
        }
        if (dto.getTotalReadings() != null) {
            session.setTotalReadings(dto.getTotalReadings());
        }
        
        return trackingSessionR.save(session);
    }

    public List<Map<String, Object>> getSessions(Long readerId, LocalDate date) {
        List<TrackingSession> sessions;
        if (readerId != null && date != null) {
            sessions = trackingSessionR.findByReaderIdAndWorkDateOrderByStartTimeDesc(readerId, date);
        } else if (readerId != null) {
            sessions = trackingSessionR.findByReaderIdOrderByStartTimeDesc(readerId);
        } else if (date != null) {
            sessions = trackingSessionR.findByWorkDateOrderByStartTimeDesc(date);
        } else {
            sessions = trackingSessionR.findAllByOrderByWorkDateDescStartTimeDesc();
        }

        return sessions.stream().map(s -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", s.getId());
            map.put("readerId", s.getReaderId());
            map.put("workDate", s.getWorkDate());
            map.put("startTime", s.getStartTime());
            map.put("endTime", s.getEndTime());
            map.put("status", s.getStatus());
            map.put("totalDistanceMeters", s.getTotalDistanceMeters());
            map.put("totalPoints", s.getTotalPoints());
            map.put("totalReadings", s.getTotalReadings());
            
            String name = usuariosR.findById(s.getReaderId())
                .map(u -> u.getNomusu())
                .orElse("ID: " + s.getReaderId());
            map.put("readerName", name);
            
            return map;
        }).collect(Collectors.toList());
    }

    public Map<String, Object> getSessionFullTrace(String sessionId) {
        TrackingSession session = trackingSessionR.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found: " + sessionId));

        List<TrackingPoint> points = trackingPointR.findBySessionIdOrderByCapturedAtAsc(sessionId);
        List<RouteDeviationEvent> deviations = routeDeviationEventR.findBySessionId(sessionId);
        List<GpsGapEvent> gaps = gpsGapEventR.findBySessionId(sessionId);

        Map<String, Object> trace = new HashMap<>();
        trace.put("session", session);
        trace.put("points", points);
        trace.put("deviations", deviations);
        trace.put("gaps", gaps);

        return trace;
    }
}
