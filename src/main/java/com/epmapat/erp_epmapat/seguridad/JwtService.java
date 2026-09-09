package com.epmapat.erp_epmapat.seguridad;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;

@Service
public class JwtService {
    private final ObjectMapper objectMapper;
    private final byte[] secret;
    private final long expirationSeconds;

    public JwtService(ObjectMapper objectMapper,
            @Value("${erp.jwt.secret}") String secret,
            @Value("${erp.jwt.expiration-seconds:28800}") long expirationSeconds) {
        this.objectMapper = objectMapper;
        this.secret = secret.getBytes(StandardCharsets.UTF_8);
        this.expirationSeconds = expirationSeconds;
    }

    public String createWebToken(Long userId, String username, String profile) {
        try {
            long now = Instant.now().getEpochSecond();
            String header = encode("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
            String payload = encode(objectMapper.writeValueAsString(java.util.Map.of(
                    "sub", String.valueOf(userId),
                    "username", username == null ? "" : username,
                    "profile", profile == null ? "" : profile,
                    "platform", "WEB",
                    "iat", now,
                    "exp", now + expirationSeconds)));
            String content = header + "." + payload;
            return content + "." + sign(content);
        } catch (Exception ex) {
            throw new IllegalStateException("No se pudo generar el token de sesión", ex);
        }
    }

    public Map<String, Object> validateWebToken(String token) {
        try {
            String[] parts = token == null ? new String[0] : token.split("\\.");
            if (parts.length != 3) throw new IllegalArgumentException("Formato JWT inválido");
            String content = parts[0] + "." + parts[1];
            byte[] expected = Base64.getUrlDecoder().decode(sign(content));
            byte[] received = Base64.getUrlDecoder().decode(parts[2]);
            if (!java.security.MessageDigest.isEqual(expected, received)) {
                throw new IllegalArgumentException("Firma JWT inválida");
            }
            @SuppressWarnings("unchecked")
            Map<String, Object> payload = objectMapper.readValue(
                    Base64.getUrlDecoder().decode(parts[1]), Map.class);
            long expiration = ((Number) payload.getOrDefault("exp", 0)).longValue();
            if (expiration <= Instant.now().getEpochSecond() || !"WEB".equals(payload.get("platform"))) {
                throw new IllegalArgumentException("JWT expirado o no corresponde a WEB");
            }
            return payload;
        } catch (Exception ex) {
            throw new IllegalArgumentException("JWT inválido", ex);
        }
    }

    private String sign(String content) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(secret, "HmacSHA256"));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
    }

    private String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }
}
