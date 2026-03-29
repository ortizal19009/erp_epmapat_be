package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CareerPlanRequest extends AuditMetadataRequest {

    @NotNull(message = "employeeId es obligatorio")
    private Long employeeId;

    @NotBlank(message = "goal es obligatorio")
    private String goal;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    private LocalDate targetDate;
    private String milestones;
}
