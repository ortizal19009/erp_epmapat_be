package com.epmapat.erp_epmapat.websocket;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class MobileWebSocketHandler extends TextWebSocketHandler {

    private final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final Map<Long, Map<String, WebSocketSession>> sessionsByUser = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        Long userId = extractUserId(session);
        if (userId != null) {
            sessionsByUser.computeIfAbsent(userId, key -> new ConcurrentHashMap<>()).put(session.getId(), session);
        }
        log.info("Mobile WebSocket conectado: {} userId={}", session.getId(), userId);
        sendJson(session, "welcome", "Canal movil conectado");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        String payload = message.getPayload();
        if (payload != null && payload.contains("\"type\":\"ping\"")) {
            sendJson(session, "pong", "heartbeat-ok");
            return;
        }
        sendJson(session, "ack", "mensaje-recibido");
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        removeSession(session);
        log.info("Mobile WebSocket cerrado: {} ({})", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        removeSession(session);
        String reason = exception != null && exception.getMessage() != null
                ? exception.getMessage()
                : exception != null ? exception.getClass().getSimpleName() : "sin detalle";
        log.warn("Error en Mobile WebSocket {}: {}", session.getId(), reason);
        super.handleTransportError(session, exception);
    }

    public void notifyAssignmentChanged(Long userId, Long idemision) {
        if (userId == null) {
            return;
        }
        Map<String, WebSocketSession> userSessions = sessionsByUser.get(userId);
        if (userSessions == null || userSessions.isEmpty()) {
            return;
        }
        String message = idemision == null
                ? "Tienes nuevas asignaciones"
                : "Tienes nuevas asignaciones para la emision " + idemision;
        userSessions.values().forEach(session -> {
            try {
                sendJson(session, "assignment_update", message);
            } catch (IOException e) {
                log.warn("No se pudo notificar assignment_update a {}: {}", session.getId(), e.getMessage());
            }
        });
    }

    public int activeConnections() {
        return sessions.size();
    }

    private void removeSession(WebSocketSession session) {
        sessions.remove(session.getId());
        Long userId = extractUserId(session);
        if (userId != null) {
            Map<String, WebSocketSession> userSessions = sessionsByUser.get(userId);
            if (userSessions != null) {
                userSessions.remove(session.getId());
                if (userSessions.isEmpty()) {
                    sessionsByUser.remove(userId);
                }
            }
        }
    }

    private Long extractUserId(WebSocketSession session) {
        URI uri = session.getUri();
        if (uri == null || uri.getQuery() == null || uri.getQuery().isBlank()) {
            return null;
        }
        for (String part : uri.getQuery().split("&")) {
            String[] tokens = part.split("=", 2);
            if (tokens.length == 2 && "userId".equalsIgnoreCase(tokens[0])) {
                try {
                    return Long.parseLong(tokens[1]);
                } catch (NumberFormatException ignored) {
                    return null;
                }
            }
        }
        return null;
    }

    private void sendJson(WebSocketSession session, String type, String message) throws IOException {
        if (!session.isOpen()) {
            return;
        }
        session.sendMessage(new TextMessage(
                "{\"type\":\"" + type + "\",\"message\":\"" + message + "\",\"ts\":" + System.currentTimeMillis() + "}"
        ));
    }
}
