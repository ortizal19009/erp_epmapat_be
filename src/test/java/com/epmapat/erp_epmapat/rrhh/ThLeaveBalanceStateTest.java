package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveBalance;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThLeaveBalanceStateTest {
    ThLeaveBalanceR balances = mock(ThLeaveBalanceR.class);
    ThLeaveMovementR movements = mock(ThLeaveMovementR.class);
    ThAuditServicio audit = mock(ThAuditServicio.class);
    ThAuditLogR logs = mock(ThAuditLogR.class);
    ThLeaveBalanceStateServicio service = new ThLeaveBalanceStateServicio(balances, movements, audit, logs);

    @Test void requiresStateVersionActorAndBoundedReasonBeforeQuerying() {
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, null, 0L, "Motivo", 8L));
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, null, "Motivo", 8L));
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, -1L, "Motivo", 8L));
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, 0L, " ", 8L));
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, 0L, "x".repeat(2001), 8L));
        assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, 0L, "Motivo", 0L));
        verifyNoInteractions(balances, movements, audit, logs);
    }

    @Test void nonexistentBalanceAndInvalidHistoryIdHaveControlledStatus() {
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, 0L, "Motivo", 8L)).getStatus().value());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.historial(1L)).getStatus().value());
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.historial(0L)).getStatus().value());
        verifyNoInteractions(movements, audit, logs);
    }

    @Test void staleVersionNeverChangesStateOrWritesAudit() {
        ThLeaveBalance b = new ThLeaveBalance(); b.setEstado(true); b.setVersion(5L);
        when(balances.findByIdForUpdate(1L)).thenReturn(Optional.of(b));
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.cambiar(1L, false, 4L, "Motivo", 8L)).getStatus().value());
        assertTrue(b.getEstado()); assertEquals(5L, b.getVersion());
        verify(balances).findByIdForUpdate(1L); verifyNoMoreInteractions(balances); verifyNoInteractions(audit, movements, logs);
    }
}
