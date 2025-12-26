package com.epmapat.erp_epmapat.DTO;

import lombok.Data;

@Data
public class LoginRequest {
    private String username;
    private String password;
    private Long userId;
      private String platform; // "WEB" o "MOBILE"
}