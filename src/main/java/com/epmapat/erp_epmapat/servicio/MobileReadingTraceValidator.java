package com.epmapat.erp_epmapat.servicio;

import java.time.ZoneId;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto;
import com.epmapat.erp_epmapat.modelo.TrackingSession;
import com.epmapat.erp_epmapat.repositorio.TrackingSessionR;

@Component
public class MobileReadingTraceValidator {
    private final TrackingSessionR sessions;

    public MobileReadingTraceValidator(TrackingSessionR sessions) {
        this.sessions = sessions;
    }

    public void validate(LecturaUploadItemDto item, Long userId) {
        if (item == null || item.getTrackingSessionId() == null) reject("Falta la sesion GPS de la lectura");
        String id = item.getTrackingSessionId();
        try {
            if (!UUID.fromString(id).toString().equalsIgnoreCase(id)) reject("La sesion GPS debe usar su UUID remoto");
        } catch (IllegalArgumentException e) {
            reject("La sesion GPS debe usar su UUID remoto");
        }
        if (!validCoordinate(item.getReadingLatitude(), -90, 90)
                || !validCoordinate(item.getReadingLongitude(), -180, 180)) reject("Faltan coordenadas GPS validas");
        Double accuracy = item.getReadingAccuracy();
        if (accuracy == null || !Double.isFinite(accuracy) || accuracy < 0) reject("Falta precision GPS valida");
        if (item.getReadingCapturedAt() == null) reject("Falta la fecha de captura GPS");
        TrackingSession session = sessions.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.BAD_REQUEST, "Sincroniza la sesion GPS antes de enviar sus lecturas"));
        if (userId == null || !userId.equals(session.getReaderId())
                || !userId.equals(item.getUsuariolectura())) reject("La lectura y la sesion GPS deben pertenecer al mismo usuario");
        if (!item.getReadingCapturedAt().toInstant().atZone(ZoneId.of("America/Guayaquil"))
                .toLocalDate().equals(session.getWorkDate())) reject("La captura GPS no corresponde a la fecha de la jornada");
    }

    private boolean validCoordinate(Double value, double min, double max) {
        return value != null && Double.isFinite(value) && value >= min && value <= max;
    }

    private void reject(String message) {
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, message);
    }
}
