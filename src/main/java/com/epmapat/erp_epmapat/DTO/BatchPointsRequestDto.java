package com.epmapat.erp_epmapat.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BatchPointsRequestDto {
    private String trackingSessionId;
    @Builder.Default
    private List<TrackingPointDto> points = new ArrayList<>();
    @Builder.Default
    private List<RouteDeviationEventDto> deviationEvents = new ArrayList<>();
    @Builder.Default
    private List<GpsGapEventDto> gapEvents = new ArrayList<>();
}
