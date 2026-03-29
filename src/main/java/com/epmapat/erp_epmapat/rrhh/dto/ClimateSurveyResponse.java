package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ClimateSurveyResponse {
    UUID id;
    String title;
    String area;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    BigDecimal participationRate;
    BigDecimal satisfactionScore;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
