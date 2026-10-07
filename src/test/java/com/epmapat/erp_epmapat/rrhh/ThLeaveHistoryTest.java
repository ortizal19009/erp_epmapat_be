package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDateTime;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveHistoryServicio;

class ThLeaveHistoryTest {
    ThLeaveRequestR requests = mock(ThLeaveRequestR.class);
    ThAuditLogR audit = mock(ThAuditLogR.class);
    ThLeaveMovementR movements = mock(ThLeaveMovementR.class);
    ThLeaveHistoryServicio service = new ThLeaveHistoryServicio(requests, audit, movements);
    ThLeaveRequest request;

    @BeforeEach void setup() {
        request = new ThLeaveRequest(); request.setIdrequest(9L); request.setEstado("SOLICITADA"); request.setTipolicencia("PERMISO");
        Personal p = new Personal(); p.setIdpersonal(7L); request.setIdpersonal_personal(p);
        when(requests.findById(9L)).thenReturn(Optional.of(request));
        when(audit.findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_REQUEST", 9L)).thenReturn(List.of());
        when(movements.findByRequestIdrequestOrderByIdmovementAsc(9L)).thenReturn(List.of());
    }

    ThAuditLog event(Long id, String action, LocalDateTime date) {
        ThAuditLog a = new ThAuditLog(); a.setIdaudit(id); a.setAccion(action); a.setFecha(date); a.setUsuario(8L); return a;
    }

    @Test void rejectsInvalidAndMissingRequestsBeforeReadingOtherEvidence() {
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.consultar(0L)).getStatus().value());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.consultar(999L)).getStatus().value());
        verifyNoInteractions(audit, movements);
    }

    @Test void historicalApprovalHasExplicitMissingEvidenceAndNeverFabricatedEvents() {
        request.setEstado("APROBADA"); request.setTipolicencia("VACACION");
        var result = service.consultar(9L);
        assertTrue(result.getEventos().isEmpty()); assertTrue(result.getMovimientos().isEmpty());
        assertTrue(result.getAdvertencias().stream().anyMatch(w -> w.contains("creación")));
        assertTrue(result.getAdvertencias().stream().anyMatch(w -> w.contains("aprobación")));
        assertTrue(result.getAdvertencias().stream().anyMatch(w -> w.contains("No existe consumo")));
        assertEquals("APROBADA", request.getEstado());
        verify(requests).findById(9L); verify(audit).findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_REQUEST", 9L);
        verify(movements).findByRequestIdrequestOrderByIdmovementAsc(9L); verifyNoMoreInteractions(requests, audit, movements);
    }

    @Test void eventsAreChronologicalWithStableTieOrderAndNullDatesLast() {
        LocalDateTime date = LocalDateTime.of(2026, 10, 6, 10, 0);
        when(audit.findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_REQUEST", 9L)).thenReturn(List.of(
                event(4L, "UNKNOWN", null), event(3L, "UPDATE", date), event(2L, "REVIEW", date), event(1L, "CREATE", date.minusHours(1))));
        var result = service.consultar(9L);
        assertEquals(List.of(1L, 2L, 3L, 4L), result.getEventos().stream().map(e -> e.getIdaudit()).toList());
        assertEquals("UNKNOWN", result.getEventos().get(3).getAccion()); assertNull(result.getEventos().get(3).getFecha());
        assertTrue(result.getAdvertencias().stream().anyMatch(w -> w.contains("sin fecha o usuario")));
    }

    @Test void nonFinancialReversalDoesNotRequireVacationDebitOrRefund() {
        request.setEstado("REVERTIDA"); request.setTipolicencia("LICENCIA");
        LocalDateTime date = LocalDateTime.of(2026, 10, 6, 10, 0);
        when(audit.findByEntidadAndIdregistroOrderByFechaDesc("TH_LEAVE_REQUEST", 9L)).thenReturn(List.of(
                event(1L, "CREATE", date), event(2L, "APPROVE", date.plusHours(1)), event(3L, "REVERSE", date.plusHours(2))));
        var result = service.consultar(9L);
        assertTrue(result.getAdvertencias().isEmpty()); assertTrue(result.getMovimientos().isEmpty());
    }
}
