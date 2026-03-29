package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class ConflictCaseRequest extends AuditMetadataRequest {

    private Long employeeId;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "description es obligatoria")
    private String description;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotBlank(message = "responsible es obligatorio")
    private String responsible;

    @NotNull(message = "openedAt es obligatorio")
    private LocalDate openedAt;

    private LocalDate resolvedAt;
    private String resolution;
}
