package com.epmapat.erp_epmapat.servicio;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;

@Service
public class TelegramNotificationService {

    private static final Logger logger = LoggerFactory.getLogger(TelegramNotificationService.class);

    private final RestTemplate restTemplate;

    @Value("${telegram.enabled:false}")
    private boolean enabled;

    @Value("${telegram.bot-token:}")
    private String botToken;

    @Value("${telegram.chat-id:}")
    private String chatId;

    public TelegramNotificationService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostConstruct
    public void init() {
        if (enabled) {
            String maskedToken = (botToken != null && botToken.length() > 10) 
                ? botToken.substring(0, 5) + "..." + botToken.substring(botToken.length() - 5) 
                : "invalid";
            logger.info("Telegram Service ENABLED. ChatId: {}, Token: {}", chatId, maskedToken);
        } else {
            logger.warn("Telegram Service DISABLED.");
        }
    }

    public void notifyBackup(boolean successful, String backupFile, long durationSeconds, String detail) {
        if (!enabled || botToken.isBlank() || chatId.isBlank()) {
            logger.debug("Notificacion de backup por Telegram omitida: configuracion incompleta o deshabilitada.");
            return;
        }

        String status = successful ? "COMPLETADO" : "FALLIDO";
        String message = String.format(
                "Respaldo ERP %s%nArchivo: %s%nDuracion: %d s%nDetalle: %s",
                status,
                backupFile,
                durationSeconds,
                detail == null || detail.isBlank() ? "Sin novedades" : detail
        );

        sendSimpleMessage(message, false);
    }

    public void sendVerificationCode(String username, String ownerName, String profile, String code) {
        if (!enabled || botToken.isBlank() || chatId.isBlank()) {
            logger.warn("Notificacion de codigo por Telegram omitida: configuracion incompleta o deshabilitada.");
            return;
        }

        String requesterRole = (profile != null && profile.toUpperCase().contains("ADMIN")) 
            ? "ADMINISTRADOR" 
            : "LECTOR";

        String message = String.format(
                "🔐 *CONTROL DE SEGURIDAD ERP*%n%n" +
                "%s: *%s*%n" +
                "Cuenta de: *%s*%n%n" +
                "SOLICITUD: Eliminación de cambio pendiente.%n%n" +
                "Código de Verificación: `%s`%n%n" +
                "⚠️ Validez: 5 minutos.",
                requesterRole, username, ownerName, code
        );

        sendSimpleMessage(message, true);
    }

    private void sendSimpleMessage(String text, boolean markdown) {
        if (!enabled || botToken == null || botToken.isBlank()) {
            logger.warn("Envio de Telegram cancelado: Bot Token no configurado o deshabilitado.");
            return;
        }
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> body = markdown 
                ? Map.of("chat_id", chatId, "text", text, "parse_mode", "Markdown")
                : Map.of("chat_id", chatId, "text", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
            String url = "https://api.telegram.org/bot" + botToken + "/sendMessage";
            
            logger.info("Enviando mensaje a Telegram chat_id: {}", chatId);
            
            restTemplate.postForEntity(url, request, String.class);
            logger.info("Mensaje enviado exitosamente a Telegram");
        } catch (Exception exception) {
            logger.error("Error al enviar mensaje a Telegram: {}. URL: https://api.telegram.org/bot{}/sendMessage", 
                exception.getMessage(), botToken.substring(0, Math.min(botToken.length(), 10)) + "...");
        }
    }
}
