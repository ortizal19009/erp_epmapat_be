package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class AuditResponse {
    UUID id;
    String auditType;
    String title;
    String findings;
    String actionPlan;
    String status;
    LocalDate auditDate;
    String responsible;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
