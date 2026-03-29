package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class EmployeeFileResponse {
    UUID id;
    Long employeeId;
    String fileType;
    String fileName;
    String fileUrl;
    LocalDate issueDate;
    LocalDate expiryDate;
    String status;
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    String createdBy;
    String updatedBy;
}
