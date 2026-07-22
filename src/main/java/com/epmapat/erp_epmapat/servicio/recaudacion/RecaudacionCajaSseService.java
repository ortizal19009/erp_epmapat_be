package com.epmapat.erp_epmapat.servicio.recaudacion;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaDTO;

@Service
public class RecaudacionCajaSseService {

    private static final Logger log = LoggerFactory.getLogger(RecaudacionCajaSseService.class);

    private static final long SSE_TIMEOUT_MS = 0L;
    private static final long HEARTBEAT_INTERVAL_SECONDS = 20L;

    private final Map<Long, Map<String, SseEmitter>> emittersPorUsuario = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> emittersGlobales = new ConcurrentHashMap<>();
    private final ScheduledExecutorService heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(new SseHeartbeatThreadFactory());

    public RecaudacionCajaSseService() {
        heartbeatExecutor.scheduleAtFixedRate(
                this::sendHeartbeats,
                HEARTBEAT_INTERVAL_SECONDS,
                HEARTBEAT_INTERVAL_SECONDS,
                TimeUnit.SECONDS);
    }

    public SseEmitter subscribe(Long idusuario, RecaudacionCajaDTO estadoInicial) {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        String emitterId = UUID.randomUUID().toString();

        emittersPorUsuario
                .computeIfAbsent(idusuario, key -> new ConcurrentHashMap<>())
                .put(emitterId, emitter);

        emitter.onCompletion(() -> removeEmitter(idusuario, emitterId));
        emitter.onTimeout(() -> removeEmitter(idusuario, emitterId));
        emitter.onError(ex -> removeEmitter(idusuario, emitterId));

        try {
            sendEvent(emitter, "connected", Map.of(
                    "status", "connected",
                    "emitterId", emitterId,
                    "idusuario", idusuario));

            if (estadoInicial != null) {
                sendEvent(emitter, "caja.estado", estadoInicial);
            }
        } catch (Exception e) {
            removeEmitter(idusuario, emitterId);
        }

        return emitter;
    }

    public void publishEstado(Long idusuario, RecaudacionCajaDTO payload) {
        publish(idusuario, "caja.estado", payload);
    }

    public void publishSecuencial(Long idusuario, RecaudacionCajaDTO payload) {
        publish(idusuario, "caja.secuencial", payload);
    }

    public SseEmitter subscribeGlobal() {
        SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
        String emitterId = UUID.randomUUID().toString();

        emittersGlobales.put(emitterId, emitter);

        emitter.onCompletion(() -> removeGlobalEmitter(emitterId));
        emitter.onTimeout(() -> removeGlobalEmitter(emitterId));
        emitter.onError(ex -> removeGlobalEmitter(emitterId));

        try {
            sendEvent(emitter, "connected", Map.of(
                    "status", "connected",
                    "emitterId", emitterId));
        } catch (Exception e) {
            removeGlobalEmitter(emitterId);
        }

        return emitter;
    }

    public void publishEstadoGlobal(RecaudacionCajaDTO payload) {
        if (payload == null || emittersGlobales.isEmpty()) {
            return;
        }

        emittersGlobales.forEach((emitterId, emitter) -> {
            if (!safeSendEvent(emitter, "caja.global.estado", payload, "global", emitterId)) {
                removeGlobalEmitter(emitterId);
            }
        });
    }

    private void publish(Long idusuario, String eventName, RecaudacionCajaDTO payload) {
        if (idusuario == null || payload == null) {
            return;
        }

        Map<String, SseEmitter> emitters = emittersPorUsuario.get(idusuario);
        if (emitters == null || emitters.isEmpty()) {
            return;
        }

        emitters.forEach((emitterId, emitter) -> {
            if (!safeSendEvent(emitter, eventName, payload, String.valueOf(idusuario), emitterId)) {
                removeEmitter(idusuario, emitterId);
            }
        });
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object payload) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(payload));
    }

    private void sendHeartbeat(SseEmitter emitter) throws IOException {
        emitter.send(SseEmitter.event()
                .name("heartbeat")
                .data(Map.of("ts", System.currentTimeMillis(), "status", "alive")));
    }

    private boolean safeSendEvent(
            SseEmitter emitter,
            String eventName,
            Object payload,
            String scope,
            String emitterId) {
        try {
            sendEvent(emitter, eventName, payload);
            return true;
        } catch (IOException ex) {
            if (isExpectedDisconnect(ex)) {
                log.debug("SSE desconectado [{}:{}] al enviar {}: {}", scope, emitterId, eventName, ex.getMessage());
            } else {
                log.warn("No se pudo enviar SSE [{}:{}] {}: {}", scope, emitterId, eventName, ex.getMessage());
            }
        } catch (IllegalStateException ex) {
            log.debug("SSE ya cerrado [{}:{}] al enviar {}: {}", scope, emitterId, eventName, ex.getMessage());
        }

        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
        return false;
    }

    private void sendHeartbeats() {
        emittersPorUsuario.forEach((idusuario, emitters) -> emitters.forEach((emitterId, emitter) -> {
            if (!safeHeartbeat(emitter, String.valueOf(idusuario), emitterId)) {
                removeEmitter(idusuario, emitterId);
            }
        }));

        emittersGlobales.forEach((emitterId, emitter) -> {
            if (!safeHeartbeat(emitter, "global", emitterId)) {
                removeGlobalEmitter(emitterId);
            }
        });
    }

    private boolean safeHeartbeat(SseEmitter emitter, String scope, String emitterId) {
        try {
            sendHeartbeat(emitter);
            return true;
        } catch (IOException ex) {
            if (isExpectedDisconnect(ex)) {
                log.debug("Heartbeat SSE desconectado [{}:{}]: {}", scope, emitterId, ex.getMessage());
            } else {
                log.warn("Heartbeat SSE falló [{}:{}]: {}", scope, emitterId, ex.getMessage());
            }
        } catch (IllegalStateException ex) {
            log.debug("Heartbeat SSE ya cerrado [{}:{}]: {}", scope, emitterId, ex.getMessage());
        }

        try {
            emitter.complete();
        } catch (Exception ignored) {
        }
        return false;
    }

    private void removeEmitter(Long idusuario, String emitterId) {
        Map<String, SseEmitter> emitters = emittersPorUsuario.get(idusuario);
        if (emitters == null) {
            return;
        }

        emitters.remove(emitterId);
        if (emitters.isEmpty()) {
            emittersPorUsuario.remove(idusuario);
        }
        log.debug("Emitter SSE removido para usuario {}", idusuario);
    }

    private void removeGlobalEmitter(String emitterId) {
        emittersGlobales.remove(emitterId);
    }

    private boolean isExpectedDisconnect(IOException ex) {
        String message = ex.getMessage();
        if (message == null) {
            return false;
        }
        String normalized = message.toLowerCase(Locale.ROOT);
        return normalized.contains("broken pipe")
                || normalized.contains("connection reset")
                || normalized.contains("connection timed out")
                || normalized.contains("forcibly closed")
                || normalized.contains("abort");
    }

    private static final class SseHeartbeatThreadFactory implements ThreadFactory {
        @Override
        public Thread newThread(Runnable runnable) {
            Thread thread = new Thread(runnable, "recaudacion-caja-sse-heartbeat");
            thread.setDaemon(true);
            return thread;
        }
    }
}
