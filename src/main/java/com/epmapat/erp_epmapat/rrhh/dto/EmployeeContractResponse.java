package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmployeeContractResponse {
    UUID id;
    Long employeeId;
    String contractNumber;
    String contractType;
    LocalDate startDate;
    LocalDate endDate;
    Boolean currentContract;
    BigDecimal salary;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
