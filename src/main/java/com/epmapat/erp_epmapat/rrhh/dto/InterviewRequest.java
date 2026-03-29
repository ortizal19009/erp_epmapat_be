package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class InterviewRequest extends AuditMetadataRequest {

    @NotNull(message = "vacancyId es obligatorio")
    private UUID vacancyId;

    @NotNull(message = "candidateId es obligatorio")
    private UUID candidateId;

    @NotBlank(message = "stage es obligatoria")
    private String stage;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "scheduledAt es obligatorio")
    private LocalDateTime scheduledAt;

    @NotBlank(message = "interviewer es obligatorio")
    private String interviewer;

    private String notes;
    private BigDecimal score;
}
