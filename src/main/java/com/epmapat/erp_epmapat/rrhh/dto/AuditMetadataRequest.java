package com.epmapat.erp_epmapat.rrhh.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class AuditMetadataRequest {

    @NotBlank(message = "createdBy es obligatorio")
    private String createdBy;

    @NotBlank(message = "updatedBy es obligatorio")
    private String updatedBy;
}
