package com.epmapat.erp_epmapat.websocket;

import java.io.IOException;
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

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.put(session.getId(), session);
        log.info("Mobile WebSocket conectado: {}", session.getId());
        sendJson(session, "welcome", "Canal móvil conectado");
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
        sessions.remove(session.getId());
        log.info("Mobile WebSocket cerrado: {} ({})", session.getId(), status);
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        sessions.remove(session.getId());
        String reason = exception != null && exception.getMessage() != null
                ? exception.getMessage()
                : exception != null
                    ? exception.getClass().getSimpleName()
                    : "sin detalle";
        log.warn("Error en Mobile WebSocket {}: {}", session.getId(), reason);
        super.handleTransportError(session, exception);
    }

    public int activeConnections() {
        return sessions.size();
    }

    private void sendJson(WebSocketSession session, String type, String message) throws IOException {
        if (!session.isOpen()) return;
        session.sendMessage(new TextMessage(
                "{\"type\":\"" + type + "\",\"message\":\"" + message + "\",\"ts\":" + System.currentTimeMillis() + "}"
        ));
    }
}
