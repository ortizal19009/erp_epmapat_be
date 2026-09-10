package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TrackingSessionDto {
    private String id;
    private Long readerId;
    private Long routeId;
    private String deviceId;
    private LocalDate workDate;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Double totalDistanceMeters;
    private Integer totalPoints;
    private Integer totalReadings;
    private Integer startBatteryLevel;
    private Integer endBatteryLevel;
}
