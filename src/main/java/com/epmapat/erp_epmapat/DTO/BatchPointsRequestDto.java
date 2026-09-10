package com.epmapat.erp_epmapat.DTO;

import java.util.List;
import lombok.Data;

@Data
public class BatchPointsRequestDto {
    private String trackingSessionId;
    private List<TrackingPointDto> points;
    private List<RouteDeviationEventDto> deviationEvents;
    private List<GpsGapEventDto> gapEvents;
}
