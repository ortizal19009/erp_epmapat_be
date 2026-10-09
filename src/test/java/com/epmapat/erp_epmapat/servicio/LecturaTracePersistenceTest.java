package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.DTO.LecturasAuditDTO;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.repositorio.LecturasR;
import com.epmapat.erp_epmapat.websocket.MobileWebSocketHandler;

class LecturaTracePersistenceTest {
    @Test void keepsPreviousTraceInAuditAndPersistsNewTraceAndDistance() {
        LecturasR dao = mock(LecturasR.class);
        AuditoriaGenericaService audit = mock(AuditoriaGenericaService.class);
        LecturaServicio service = new LecturaServicio();
        ReflectionTestUtils.setField(service, "dao", dao);
        ReflectionTestUtils.setField(service, "auditoriaService", audit);
        ReflectionTestUtils.setField(service, "mobileWebSocketHandler", mock(MobileWebSocketHandler.class));
        Lecturas original = new Lecturas();
        original.setIdlectura(123L);
        original.setTrackingSessionId("previous-session");
        original.setReadingLatitude(-0.3);
        original.setReadingLongitude(-78.4);
        original.setReadingAccuracy(10.0);
        original.setDistanceFromMeterMeters(50.0);
        original.setDistanceStatus("WARNING");
        when(dao.findById(123L)).thenReturn(Optional.of(original));
        when(dao.save(any(Lecturas.class))).thenAnswer(invocation -> invocation.getArgument(0));
        Lecturas incoming = new Lecturas();
        incoming.setTrackingSessionId("new-session");
        incoming.setReadingLatitude(-0.25);
        incoming.setReadingLongitude(-78.5);
        incoming.setReadingAccuracy(6.0);
        incoming.setReadingCapturedAt(Date.from(Instant.parse("2026-10-08T19:00:00Z")));
        incoming.setDistanceFromMeterMeters(12.0);
        incoming.setDistanceStatus("NORMAL");

        Lecturas result = service.actualizarLecturaConAuditoria(123L, incoming, 7L, "mobile", "MODIFICACION");
        assertEquals(incoming.getTrackingSessionId(), result.getTrackingSessionId());
        assertEquals(incoming.getReadingLatitude(), result.getReadingLatitude());
        assertEquals(incoming.getReadingLongitude(), result.getReadingLongitude());
        assertEquals(incoming.getReadingAccuracy(), result.getReadingAccuracy());
        assertEquals(incoming.getReadingCapturedAt(), result.getReadingCapturedAt());
        assertEquals(12.0, result.getDistanceFromMeterMeters());
        assertEquals("NORMAL", result.getDistanceStatus());
        ArgumentCaptor<Object> previous = ArgumentCaptor.forClass(Object.class);
        verify(audit).saveAudit(eq("lecturas"), eq(123L), previous.capture(), eq(7L), eq("mobile"), eq("MODIFICACION"));
        LecturasAuditDTO snapshot = (LecturasAuditDTO) previous.getValue();
        assertEquals("previous-session", snapshot.getTrackingSessionId());
        assertEquals(-0.3, snapshot.getReadingLatitude());
        assertEquals(-78.4, snapshot.getReadingLongitude());
        assertEquals(10.0, snapshot.getReadingAccuracy());
        assertEquals(50.0, snapshot.getDistanceFromMeterMeters());
        assertEquals("WARNING", snapshot.getDistanceStatus());
    }
}
