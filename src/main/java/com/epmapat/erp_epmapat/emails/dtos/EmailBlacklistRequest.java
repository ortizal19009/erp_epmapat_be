package com.epmapat.erp_epmapat.emails.dtos;

import com.epmapat.erp_epmapat.emails.model.EmailBlacklistType;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class EmailBlacklistRequest {
    @NotNull
    public EmailBlacklistType type;

    @NotBlank
    public String value;

    public String reason;
    public boolean active = true;
}
