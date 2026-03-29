package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class PayrollResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    LocalDate periodStart;
    LocalDate periodEnd;
    BigDecimal grossAmount;
    BigDecimal totalBenefits;
    BigDecimal totalDeductions;
    BigDecimal netAmount;
    BigDecimal overtimeHours;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
