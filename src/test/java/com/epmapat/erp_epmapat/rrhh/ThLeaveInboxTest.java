package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThLeaveRequestR;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveInboxServicio;

class ThLeaveInboxTest {
    ThLeaveRequestR requests = mock(ThLeaveRequestR.class);
    ThLeaveInboxServicio service = new ThLeaveInboxServicio(requests);

    @Test void calendarRejectsMissingReversedAndOversizedRangesBeforeQuerying() {
        LocalDate start = LocalDate.of(2026, 10, 1);
        for (LocalDate[] dates : new LocalDate[][] {{null, start}, {start, null}, {start, start.minusDays(1)},
                {start, start.plusDays(42)}, {LocalDate.of(1899, 12, 31), LocalDate.of(1900, 1, 1)}})
            assertEquals(400, assertThrows(ResponseStatusException.class,
                    () -> service.calendario(null, null, dates[0], dates[1], 0)).getStatus().value());
        verifyNoInteractions(requests);
    }

    @Test void calendarUsesApprovedStateAndBoundedPageAndRejectsInvalidFilters() {
        when(requests.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(org.springframework.data.domain.Pageable.class)))
                .thenReturn(org.springframework.data.domain.Page.empty(org.springframework.data.domain.PageRequest.of(2, 100)));
        LocalDate start = LocalDate.of(2026, 10, 1);
        var page = service.calendario(" permiso ", 7L, start, start.plusDays(41), 2);
        assertEquals(2, page.getPagina()); assertEquals(100, page.getTamano());
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.calendario("UNKNOWN", 7L, start, start, 0)).getStatus().value());
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.calendario(null, 0L, start, start, 0)).getStatus().value());
    }

    @Test void rejectsUnknownStateAndTypeBeforeQuerying() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.consultar("PAGADA", null, null, null, null, 0, 20)).getStatus().value());
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.consultar("TODAS", "DESCONOCIDO", null, null, null, 0, 20)).getStatus().value());
        verifyNoInteractions(requests);
    }

    @Test void rejectsUnboundedOrInvalidPagesBeforeQuerying() {
        for (int[] values : new int[][] {{-1, 20}, {100001, 20}, {0, 0}, {0, 101}})
            assertEquals(400, assertThrows(ResponseStatusException.class,
                    () -> service.consultar("SOLICITADA", null, null, null, null, values[0], values[1])).getStatus().value());
        verifyNoInteractions(requests);
    }

    @Test void rejectsReversedDatesAndInvalidPersonalBeforeQuerying() {
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.consultar(null, null, null, LocalDate.of(2026, 10, 2), LocalDate.of(2026, 10, 1), 0, 20)).getStatus().value());
        assertEquals(400, assertThrows(ResponseStatusException.class,
                () -> service.consultar(null, null, 0L, null, null, 0, 20)).getStatus().value());
        verifyNoInteractions(requests);
    }
}
