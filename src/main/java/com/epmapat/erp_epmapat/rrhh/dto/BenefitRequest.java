package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class BenefitRequest extends AuditMetadataRequest {

    private Long employeeId;

    @NotBlank(message = "benefitType es obligatorio")
    private String benefitType;

    @NotBlank(message = "name es obligatorio")
    private String name;

    @NotNull(message = "cost es obligatorio")
    private BigDecimal cost;

    @NotNull(message = "startDate es obligatoria")
    private LocalDate startDate;

    private LocalDate endDate;

    @NotBlank(message = "status es obligatorio")
    private String status;
}
