package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PayrollRequest extends AuditMetadataRequest {

    @NotNull(message = "employeeId es obligatorio")
    private Long employeeId;

    @NotNull(message = "periodStart es obligatorio")
    private LocalDate periodStart;

    @NotNull(message = "periodEnd es obligatorio")
    private LocalDate periodEnd;

    @NotNull(message = "grossAmount es obligatorio")
    private BigDecimal grossAmount;

    @NotNull(message = "totalBenefits es obligatorio")
    private BigDecimal totalBenefits;

    @NotNull(message = "totalDeductions es obligatorio")
    private BigDecimal totalDeductions;

    @NotNull(message = "netAmount es obligatorio")
    private BigDecimal netAmount;

    @NotNull(message = "overtimeHours es obligatorio")
    private BigDecimal overtimeHours;

    @NotBlank(message = "status es obligatorio")
    private String status;
}
