package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class WellbeingProgramResponse {
    UUID id;
    String name;
    String area;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    BigDecimal participationRate;
    BigDecimal cost;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
