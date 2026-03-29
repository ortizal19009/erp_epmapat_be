package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ConflictCaseResponse {
    UUID id;
    Long employeeId;
    String employeeName;
    String title;
    String description;
    String status;
    String responsible;
    LocalDate openedAt;
    LocalDate resolvedAt;
    String resolution;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
