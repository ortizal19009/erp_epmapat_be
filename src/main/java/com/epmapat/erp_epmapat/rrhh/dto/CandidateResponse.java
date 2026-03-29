package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class CandidateResponse {
    UUID id;
    UUID vacancyId;
    String vacancyCode;
    String firstName;
    String lastName;
    String identification;
    String email;
    String phone;
    String stage;
    String status;
    LocalDate appliedAt;
    BigDecimal score;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
