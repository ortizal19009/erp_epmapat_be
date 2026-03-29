package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MentoringResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String mentorName;
    String coachType;
    LocalDate startDate;
    LocalDate endDate;
    String status;
    String notes;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
