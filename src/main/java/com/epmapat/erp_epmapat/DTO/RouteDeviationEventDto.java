package com.epmapat.erp_epmapat.DTO;

import lombok.Data;

@Data
public class RouteDeviationEventDto {
    private Long trackingSessionId;
    private Long readerId;
    private Long exitTime;
    private Long returnTime;
    private Double exitLatitude;
    private Double exitLongitude;
    private Double maximumDistanceMeters;
    private Long durationSeconds;
}
