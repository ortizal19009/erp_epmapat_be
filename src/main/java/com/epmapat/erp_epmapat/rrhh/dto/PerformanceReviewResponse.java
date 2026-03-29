package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PerformanceReviewResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String period;
    BigDecimal score;
    String reviewer;
    LocalDate reviewDate;
    String status;
    String comments;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
