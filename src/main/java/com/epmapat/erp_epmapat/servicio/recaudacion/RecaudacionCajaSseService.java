package com.epmapat.erp_epmapat.servicio.recaudacion;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.epmapat.erp_epmapat.DTO.recaudacion.RecaudacionCajaDTO;

@Service
public class RecaudacionCajaSseService {

    private static final Logger log = LoggerFactory.getLogger(RecaudacionCajaSseService.class);

    private static final long SSE_TIMEOUT_MS = 0L;

    private final Map<Long, Map<String, SseEmitter>> emittersPorUsuario = new ConcurrentHashMap<>();
    private final Map<String, SseEmitter> emittersGlobales = new ConcurrentHashMap<>();

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
            try {
                sendEvent(emitter, "caja.global.estado", payload);
            } catch (Exception e) {
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
            try {
                sendEvent(emitter, eventName, payload);
            } catch (Exception e) {
                removeEmitter(idusuario, emitterId);
            }
        });
    }

    private void sendEvent(SseEmitter emitter, String eventName, Object payload) throws IOException {
        emitter.send(SseEmitter.event()
                .name(eventName)
                .data(payload));
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
}
