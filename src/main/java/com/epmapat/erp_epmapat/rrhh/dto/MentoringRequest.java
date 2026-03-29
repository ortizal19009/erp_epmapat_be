package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class MentoringRequest extends AuditMetadataRequest {

    @NotNull(message = "employeeId es obligatorio")
    private Long employeeId;

    @NotBlank(message = "mentorName es obligatorio")
    private String mentorName;

    @NotBlank(message = "coachType es obligatorio")
    private String coachType;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    private String notes;
}
