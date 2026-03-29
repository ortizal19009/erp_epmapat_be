package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.Data;

@Data
public class EmployeeRequest extends AuditMetadataRequest {

    @NotBlank(message = "code es obligatorio")
    private String code;

    @NotBlank(message = "firstName es obligatorio")
    private String firstName;

    @NotBlank(message = "lastName es obligatorio")
    private String lastName;

    @NotBlank(message = "identification es obligatorio")
    private String identification;

    @Email(message = "email no es válido")
    @NotBlank(message = "email es obligatorio")
    private String email;

    private String phone;
    private String address;

    @NotBlank(message = "area es obligatoria")
    private String area;

    @NotBlank(message = "dependency es obligatoria")
    private String dependency;

    @NotBlank(message = "jobTitle es obligatorio")
    private String jobTitle;

    @NotBlank(message = "contractType es obligatorio")
    private String contractType;

    @NotBlank(message = "employmentStatus es obligatorio")
    private String employmentStatus;

    @NotNull(message = "hireDate es obligatoria")
    private LocalDate hireDate;

    private LocalDate terminationDate;
    private LocalDate birthDate;
    private String professionalTitle;
    private Boolean active;
}
