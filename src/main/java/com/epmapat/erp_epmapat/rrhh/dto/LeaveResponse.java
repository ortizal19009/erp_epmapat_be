package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LeaveResponse {
    UUID id;
    Long employeeId;
    String leaveType;
    String absenceType;
    LocalDate startDate;
    LocalDate endDate;
    BigDecimal days;
    String status;
    String reason;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
