package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class BenefitResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String benefitType;
    String name;
    BigDecimal cost;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
