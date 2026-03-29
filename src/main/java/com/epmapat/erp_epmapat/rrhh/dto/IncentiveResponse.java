package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class IncentiveResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String incentiveType;
    String title;
    BigDecimal amount;
    LocalDate grantedDate;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
