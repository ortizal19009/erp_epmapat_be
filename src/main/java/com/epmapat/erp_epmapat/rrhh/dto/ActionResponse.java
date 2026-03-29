package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ActionResponse {
    UUID id;
    Long employeeId;
    String actionType;
    LocalDate effectiveDate;
    String status;
    String description;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
