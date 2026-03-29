package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PerformanceReviewRequest extends AuditMetadataRequest {

    @NotNull(message = "employeeId es obligatorio")
    private Long employeeId;

    @NotBlank(message = "period es obligatorio")
    private String period;

    @NotNull(message = "score es obligatorio")
    private BigDecimal score;

    @NotBlank(message = "reviewer es obligatorio")
    private String reviewer;

    @NotNull(message = "reviewDate es obligatorio")
    private LocalDate reviewDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    private String comments;
}
