package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class TrainingPlanResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String area;
    String title;
    String description;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal hours;
    BigDecimal cost;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
