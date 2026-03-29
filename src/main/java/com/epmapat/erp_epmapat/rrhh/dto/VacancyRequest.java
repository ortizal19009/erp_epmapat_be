package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class VacancyRequest extends AuditMetadataRequest {

    @NotBlank(message = "code es obligatorio")
    private String code;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "area es obligatoria")
    private String area;

    @NotBlank(message = "dependency es obligatoria")
    private String dependency;

    @NotBlank(message = "stage es obligatoria")
    private String stage;

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotNull(message = "openDate es obligatoria")
    private LocalDate openDate;

    private LocalDate closeDate;
    private BigDecimal budgetedSalary;
}
