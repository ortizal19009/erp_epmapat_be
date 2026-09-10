package com.epmapat.erp_epmapat.DTO;

import lombok.Data;

@Data
public class GpsGapEventDto {
    private Long trackingSessionId;
    private Long startTime;
    private Long endTime;
    private Long durationSeconds;
    private Double lastLatitude;
    private Double lastLongitude;
}
