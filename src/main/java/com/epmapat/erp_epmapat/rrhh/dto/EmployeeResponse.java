package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmployeeResponse {
    Long id;
    String code;
    String firstName;
    String lastName;
    String identification;
    String email;
    String phone;
    String address;
    String area;
    String dependency;
    String jobTitle;
    String contractType;
    String employmentStatus;
    LocalDate hireDate;
    LocalDate terminationDate;
    LocalDate birthDate;
    String professionalTitle;
    Boolean active;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
