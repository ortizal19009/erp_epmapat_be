package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class TrackingPointDto {
    private Long id;
    private String clientPointId;
    private String trackingSessionId;
    private Long readerId;
    private Double latitude;
    private Double longitude;
    private Double accuracy;
    private Double altitude;
    private Double speed;
    private Double bearing;
    private Integer batteryLevel;
    private Boolean isMockLocation;
    private String routeStatus;
    private LocalDateTime capturedAt;
}
