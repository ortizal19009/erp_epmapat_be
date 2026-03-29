package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CareerPlanResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String goal;
    String status;
    LocalDate startDate;
    LocalDate targetDate;
    String milestones;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
