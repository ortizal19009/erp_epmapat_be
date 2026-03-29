package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class IncentiveRequest extends AuditMetadataRequest {

    private Long employeeId;

    @NotBlank(message = "incentiveType es obligatorio")
    private String incentiveType;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotNull(message = "amount es obligatorio")
    private BigDecimal amount;

    @NotNull(message = "grantedDate es obligatoria")
    private LocalDate grantedDate;

    @NotBlank(message = "status es obligatorio")
    private String status;
}
