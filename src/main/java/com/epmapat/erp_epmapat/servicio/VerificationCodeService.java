package com.epmapat.erp_epmapat.servicio;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lombok.Data;

@Service
public class VerificationCodeService {

    private final Map<String, CodeData> codes = new ConcurrentHashMap<>();
    private final Random random = new Random();

    public String generateCode(String userId) {
        String code = String.format("%06d", random.nextInt(1000000));
        codes.put(userId, new CodeData(code, LocalDateTime.now().plusMinutes(5)));
        return code;
    }

    public boolean verifyCode(String userId, String code) {
        CodeData data = codes.get(userId);
        if (data == null) {
            return false;
        }
        if (LocalDateTime.now().isAfter(data.getExpiry())) {
            codes.remove(userId);
            return false;
        }
        boolean isValid = data.getCode().equals(code);
        if (isValid) {
            codes.remove(userId);
        }
        return isValid;
    }

    @Data
    @AllArgsConstructor
    private static class CodeData {
        private String code;
        private LocalDateTime expiry;
    }
}
