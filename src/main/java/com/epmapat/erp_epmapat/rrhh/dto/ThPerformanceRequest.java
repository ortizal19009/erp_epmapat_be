package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import javax.validation.constraints.*;
import lombok.Data;

@Data
public class ThPerformanceRequest {
    @NotNull @Positive private Long idpersonal;
    @NotBlank @Size(max = 40) private String periodo;
    @NotNull private LocalDate fecha;
    @NotNull @DecimalMin("0") @DecimalMax("100") @Digits(integer = 3, fraction = 2)
    private BigDecimal puntaje;
    @NotBlank @Size(max = 120) private String evaluador;
    @NotBlank @Size(max = 2000) private String observaciones;
    @Size(max = 2000) private String planMejora;
}
