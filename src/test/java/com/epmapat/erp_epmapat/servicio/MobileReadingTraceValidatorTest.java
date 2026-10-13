package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto;
import com.epmapat.erp_epmapat.modelo.TrackingSession;
import com.epmapat.erp_epmapat.repositorio.TrackingSessionR;

class MobileReadingTraceValidatorTest {
    @Test void missingGpsFlagDoesNotBypassValidationWhenCoordinatesAreValid() {
        item.setAllowMissingGps(true); item.setOperationId(java.util.UUID.randomUUID().toString());
        item.setTrackingSessionId("42");
        assertThrows(ResponseStatusException.class,()->validator.validate(item,7L));
    }
    @Test void explicitMissingGpsAllowsDataOnlyButStillRequiresOwnerAndOperation() {
        item.setReadingLatitude(null); item.setTrackingSessionId(null); item.setReadingCapturedAt(null);
        item.setAllowMissingGps(true); item.setOperationId(java.util.UUID.randomUUID().toString());
        assertDoesNotThrow(() -> validator.validate(item,7L));
        verify(sessions,never()).findById(anyString());
        assertThrows(ResponseStatusException.class,()->validator.validate(item,99L));
        item.setOperationId(null);
        assertThrows(ResponseStatusException.class,()->validator.validate(item,7L));
    }
    private final TrackingSessionR sessions = mock(TrackingSessionR.class);
    private final MobileReadingTraceValidator validator = new MobileReadingTraceValidator(sessions);
    private LecturaUploadItemDto item;

    @BeforeEach void setup() {
        item = new LecturaUploadItemDto();
        item.setTrackingSessionId("101ea23a-4977-48b9-b35b-41c1c8d4fd72");
        item.setUsuariolectura(7L);
        item.setReadingLatitude(-0.25);
        item.setReadingLongitude(-78.5);
        item.setReadingAccuracy(6.0);
        item.setReadingCapturedAt(Date.from(Instant.parse("2026-10-09T00:30:00Z")));
        when(sessions.findById(item.getTrackingSessionId())).thenReturn(Optional.of(TrackingSession.builder()
                .id(item.getTrackingSessionId()).readerId(7L).workDate(LocalDate.of(2026, 10, 8)).build()));
    }
    @Test void acceptsOfflineReadingUsingGuayaquilWorkDate() {
        assertDoesNotThrow(() -> validator.validate(item, 7L));
    }
    @Test void rejectsLocalSessionId() {
        item.setTrackingSessionId("42");
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
    }
    @Test void rejectsMissingCoordinates() {
        item.setReadingLatitude(null);
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
    }
    @Test void rejectsInvalidAccuracy() {
        item.setReadingAccuracy(Double.NaN);
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
    }
    @Test void meterReferenceMustNotMasqueradeAsCapturedGps() {
        item.setReadingSource("METER_LOCATION");
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
        item.setReadingSource("CAPTURED");
        item.setReadingCaptureAgeMillis(30001L);
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
        item.setReadingCaptureAgeMillis(1000L);
        assertDoesNotThrow(() -> validator.validate(item, 7L));
    }
    @Test void rejectsUnknownSession() {
        when(sessions.findById(item.getTrackingSessionId())).thenReturn(Optional.empty());
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
    }
    @Test void rejectsDifferentReaderOrWorkDate() {
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 8L));
        item.setReadingCapturedAt(Date.from(Instant.parse("2026-10-09T12:00:00Z")));
        assertThrows(ResponseStatusException.class, () -> validator.validate(item, 7L));
    }
}
