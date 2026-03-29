package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class InterviewResponse {
    UUID id;
    UUID vacancyId;
    String vacancyCode;
    UUID candidateId;
    String candidateName;
    String stage;
    String status;
    LocalDateTime scheduledAt;
    String interviewer;
    String notes;
    BigDecimal score;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
