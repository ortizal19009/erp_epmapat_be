package com.epmapat.erp_epmapat.emails.dtos;

import com.epmapat.erp_epmapat.emails.model.EmailAccountSecurityType;
import com.epmapat.erp_epmapat.emails.model.EmailAccountTransportType;
import com.epmapat.erp_epmapat.emails.model.EmailType;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

public class EmailAccountRequest {
    @NotBlank
    public String code;

    @NotBlank
    public String name;

    public String provider;

    @NotBlank
    public String fromAddress;

    public String fromName;
    public String replyTo;

    public String host;

    @Min(1)
    @Max(65535)
    public Integer port;

    public String protocol;

    public EmailAccountSecurityType securityType = EmailAccountSecurityType.STARTTLS;

    public EmailAccountTransportType transportType = EmailAccountTransportType.SMTP;

    public boolean authRequired = true;
    public String username;
    public String password;
    public String apiUrl;
    public String apiAuthHeader;
    public String apiAuthScheme;
    public String apiKey;
    public boolean active = true;
    public boolean defaultAccount;
    public EmailType defaultForType;
}
