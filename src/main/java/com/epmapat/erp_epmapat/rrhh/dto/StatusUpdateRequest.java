package com.epmapat.erp_epmapat.rrhh.dto;

import javax.validation.constraints.NotBlank;

import lombok.Data;

@Data
public class StatusUpdateRequest {

    @NotBlank(message = "status es obligatorio")
    private String status;

    @NotBlank(message = "updatedBy es obligatorio")
    private String updatedBy;
}
