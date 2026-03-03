package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String username;
    private Long userId;
    private String profile;
    private List<String> modules;
}
