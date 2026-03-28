package com.epmapat.erp_epmapat.emails.dtos;

import com.epmapat.erp_epmapat.emails.model.EmailStatus;
import com.epmapat.erp_epmapat.emails.model.EmailType;

import java.time.OffsetDateTime;
import java.util.UUID;

public class EmailResponse {
    public UUID id;
    public EmailType type;
    public EmailStatus status;
    public String subject;
    public String correlationId;
    public Long accountId;
    public String accountCode;
    public String accountName;
    public String fromAddress;
    public int attempts;
    public String lastError;
    public OffsetDateTime createdAt;
    public OffsetDateTime sentAt;
}
