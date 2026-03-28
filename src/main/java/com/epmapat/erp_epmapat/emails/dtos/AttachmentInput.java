package com.epmapat.erp_epmapat.emails.dtos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class AttachmentInput {
    @NotBlank public String name;
    @NotBlank public String contentType;
    @NotNull public String base64;
}
