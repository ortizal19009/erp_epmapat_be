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
        if (allowsPendingGps(item)) {
            if (userId == null || !userId.equals(item.getUsuariolectura())) reject("La lectura debe pertenecer al mismo usuario");
            if (item.getOperationId() == null) reject("Falta operationId para confirmar la lectura sin GPS");
            return;
        }
        if (item == null || item.getTrackingSessionId() == null) reject("Falta la sesion GPS de la lectura");
        if (item.getReadingSource() != null && !"CAPTURED".equals(item.getReadingSource()))
            throw new com.epmapat.erp_epmapat.excepciones.MobileSyncValidationException("GPS_SOURCE_REQUIRES_REVIEW", "La fuente GPS requiere revision; conserva la captura");
        if (item.getReadingCaptureAgeMillis() != null && (item.getReadingCaptureAgeMillis() < 0 || item.getReadingCaptureAgeMillis() > 30000))
            throw new com.epmapat.erp_epmapat.excepciones.MobileSyncValidationException("GPS_CAPTURE_TOO_OLD", "La antiguedad GPS al capturar no es valida");
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
                new com.epmapat.erp_epmapat.excepciones.MobileSyncValidationException("SESSION_NOT_REGISTERED", "Sincroniza la sesion GPS antes de enviar sus lecturas"));
        if (userId == null || !userId.equals(session.getReaderId())
                || !userId.equals(item.getUsuariolectura())) reject("La lectura y la sesion GPS deben pertenecer al mismo usuario");
        java.time.LocalDate captureDate = item.getReadingCapturedAt().toInstant()
                .atZone(ZoneId.of("America/Guayaquil")).toLocalDate();
        if (!captureDate.equals(session.getWorkDate())) {
            reject("La captura GPS no corresponde a la fecha de la jornada: captura=" + captureDate
                    + ", jornada=" + session.getWorkDate() + ", sesion=" + id
                    + ". Conserva la lectura y revisa su sesion original");
        }
    }

    public boolean allowsPendingGps(LecturaUploadItemDto item) {
        return item != null && Boolean.TRUE.equals(item.getAllowMissingGps())
            && (!validCoordinate(item.getReadingLatitude(), -90, 90) || !validCoordinate(item.getReadingLongitude(), -180, 180));
    }

    private boolean validCoordinate(Double value, double min, double max) {
        return value != null && Double.isFinite(value) && value >= min && value <= max;
    }

    private void reject(String message) {
        String code = message.contains("UUID") ? "SESSION_UUID_INVALID"
                : message.contains("fecha de la jornada") ? "SESSION_CAPTURE_DATE_MISMATCH"
                : message.contains("usuario") ? "SESSION_OWNER_MISMATCH"
                : message.contains("coordenadas") ? "GPS_COORDINATES_INVALID"
                : message.contains("precision") ? "GPS_ACCURACY_INVALID"
                : message.contains("fecha") ? "GPS_CAPTURE_TIME_MISSING" : "SESSION_MISSING";
        throw new com.epmapat.erp_epmapat.excepciones.MobileSyncValidationException(code, message);
    }
}
