package com.epmapat.erp_epmapat.sri.services;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public class RetencionSseService {

   private static final long SSE_TIMEOUT_MS = 0L;

   private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

   public SseEmitter subscribe() {
      SseEmitter emitter = new SseEmitter(SSE_TIMEOUT_MS);
      String emitterId = UUID.randomUUID().toString();
      emitters.put(emitterId, emitter);

      emitter.onCompletion(() -> emitters.remove(emitterId));
      emitter.onTimeout(() -> emitters.remove(emitterId));
      emitter.onError(ex -> emitters.remove(emitterId));

      try {
         emitter.send(SseEmitter.event()
               .name("connected")
               .data(Map.of("status", "connected", "emitterId", emitterId)));
      } catch (IOException e) {
         emitters.remove(emitterId);
      }

      return emitter;
   }

   public void publishEstadoActualizado(Map<String, Object> payload) {
      emitters.forEach((id, emitter) -> {
         try {
            emitter.send(SseEmitter.event()
                  .name("retencion.actualizada")
                  .data(payload));
         } catch (IOException e) {
            emitter.complete();
            emitters.remove(id);
         }
      });
   }
}
