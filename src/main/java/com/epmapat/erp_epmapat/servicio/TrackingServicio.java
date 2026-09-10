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

@Service
public class TrackingServicio {

    @Autowired
    private TrackingSessionR trackingSessionR;

    @Autowired
    private TrackingPointR trackingPointR;

    @Autowired
    private RouteDeviationEventR routeDeviationEventR;

    @Autowired
    private GpsGapEventR gpsGapEventR;

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
        TrackingSession session = trackingSessionR.findById(dto.getTrackingSessionId())
                .orElseThrow(() -> new RuntimeException("Session not found: " + dto.getTrackingSessionId()));

        List<TrackingPoint> points = dto.getPoints().stream()
            .filter(pDto -> pDto.getClientPointId() == null || !trackingPointR.existsByClientPointId(pDto.getClientPointId()))
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

        trackingPointR.saveAll(points);

        if (dto.getDeviationEvents() != null && !dto.getDeviationEvents().isEmpty()) {
            List<RouteDeviationEvent> deviations = dto.getDeviationEvents().stream().map(dDto ->
                RouteDeviationEvent.builder()
                    .session(session)
                    .readerId(dDto.getReaderId())
                    .exitTime(dDto.getExitTime())
                    .returnTime(dDto.getReturnTime())
                    .exitLatitude(dDto.getExitLatitude())
                    .exitLongitude(dDto.getExitLongitude())
                    .maximumDistanceMeters(dDto.getMaximumDistanceMeters())
                    .durationSeconds(dDto.getDurationSeconds())
                    .build()
            ).collect(Collectors.toList());
            routeDeviationEventR.saveAll(deviations);
        }

        if (dto.getGapEvents() != null && !dto.getGapEvents().isEmpty()) {
            List<GpsGapEvent> gaps = dto.getGapEvents().stream().map(gDto ->
                GpsGapEvent.builder()
                    .session(session)
                    .startTime(gDto.getStartTime())
                    .endTime(gDto.getEndTime())
                    .durationSeconds(gDto.getDurationSeconds())
                    .lastLatitude(gDto.getLastLatitude())
                    .lastLongitude(gDto.getLastLongitude())
                    .build()
            ).collect(Collectors.toList());
            gpsGapEventR.saveAll(gaps);
        }

        // Update session stats
        session.setTotalPoints(session.getTotalPoints() + points.size());
        trackingSessionR.save(session);
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
}
