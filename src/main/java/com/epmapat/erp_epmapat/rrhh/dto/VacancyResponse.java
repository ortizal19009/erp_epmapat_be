package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class VacancyResponse {
    UUID id;
    String code;
    String title;
    String area;
    String dependency;
    String stage;
    String status;
    LocalDate openDate;
    LocalDate closeDate;
    BigDecimal budgetedSalary;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
