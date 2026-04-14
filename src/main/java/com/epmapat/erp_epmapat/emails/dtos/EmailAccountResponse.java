package com.epmapat.erp_epmapat.emails.dtos;

import com.epmapat.erp_epmapat.emails.model.EmailAccountSecurityType;
import com.epmapat.erp_epmapat.emails.model.EmailAccountTransportType;
import com.epmapat.erp_epmapat.emails.model.EmailType;

import java.time.OffsetDateTime;

public class EmailAccountResponse {
    public Long id;
    public String code;
    public String name;
    public String provider;
    public String fromAddress;
    public String fromName;
    public String replyTo;
    public EmailAccountTransportType transportType;
    public String host;
    public Integer port;
    public String protocol;
    public EmailAccountSecurityType securityType;
    public boolean authRequired;
    public String username;
    public String password;
    public boolean hasPassword;
    public String apiUrl;
    public String apiAuthHeader;
    public String apiAuthScheme;
    public boolean hasApiKey;
    public boolean active;
    public boolean defaultAccount;
    public EmailType defaultForType;
    public OffsetDateTime createdAt;
    public OffsetDateTime updatedAt;
}
