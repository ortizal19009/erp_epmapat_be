package com.epmapat.erp_epmapat.controlador;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.servicio.TelegramNotificationService;
import com.epmapat.erp_epmapat.servicio.VerificationCodeService;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/verification")
@RequiredArgsConstructor
@Slf4j
public class VerificationController {

    private final VerificationCodeService verificationCodeService;
    private final TelegramNotificationService telegramNotificationService;
    private final UsuariosR usuariosR;

    @PostMapping("/request")
    public ResponseEntity<?> requestToken(@RequestBody Map<String, String> request) {
        log.info("Solicitud de codigo de verificacion recibida: {}", request);
        String userIdStr = request.get("userId");
        String username = request.get("username");
        String ownerName = request.getOrDefault("ownerName", "Desconocido");
        String profile = request.get("profile");

        if (userIdStr == null || username == null) {
            log.error("Solicitud invalida: faltan datos");
            return ResponseEntity.badRequest().body("userId and username are required");
        }

        String displayName = username;
        try {
            Long userId = Long.parseLong(userIdStr);
            Usuarios user = usuariosR.findById(userId).orElse(null);
            if (user != null && user.getNomusu() != null) {
                displayName = user.getNomusu();
            }
        } catch (Exception e) {
            log.warn("No se pudo obtener el nombre real del usuario {}: {}", userIdStr, e.getMessage());
        }

        String code = verificationCodeService.generateCode(userIdStr);
        log.info("Generado codigo {} para usuario {}", code, userIdStr);
        telegramNotificationService.sendVerificationCode(displayName, ownerName, profile, code);

        return ResponseEntity.ok(Map.of("message", "Code sent to Telegram"));
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyToken(@RequestBody Map<String, String> request) {
        String userId = request.get("userId");
        String code = request.get("code");

        if (userId == null || code == null) {
            return ResponseEntity.badRequest().body("userId and code are required");
        }

        boolean isValid = verificationCodeService.verifyCode(userId, code);
        return ResponseEntity.ok(Map.of("valid", isValid));
    }
}
