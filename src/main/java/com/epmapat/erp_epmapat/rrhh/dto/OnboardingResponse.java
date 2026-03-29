package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class OnboardingResponse {
    UUID id;
    Long employeeId;
    UUID candidateId;
    String employeeName;
    String candidateName;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    String owner;
    String notes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
