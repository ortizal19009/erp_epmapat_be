package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class PolicyRequest extends AuditMetadataRequest {

    @NotBlank(message = "code es obligatorio")
    private String code;

    @NotBlank(message = "title es obligatorio")
    private String title;

    @NotBlank(message = "version es obligatoria")
    private String version;

    @NotNull(message = "effectiveDate es obligatoria")
    private LocalDate effectiveDate;

    @NotBlank(message = "status es obligatorio")
    private String status;

    private String description;
}
