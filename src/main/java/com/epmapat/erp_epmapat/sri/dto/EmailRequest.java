package com.epmapat.erp_epmapat.sri.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class EmailRequest {
    private String toEmail;
    private String subject;
    private String body;
    private MultipartFile xmlFile;
}