package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.any;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThLeaveRequestServicioTest {
    ThLeaveRequestR requests = mock(ThLeaveRequestR.class);
    PersonalR personal = mock(PersonalR.class);
    ThLeaveBalanceR balances = mock(ThLeaveBalanceR.class);
    ThAuditServicio audit = mock(ThAuditServicio.class);
    ThLeaveMovementR ledger = mock(ThLeaveMovementR.class);
    ThLeaveMovementServicio movements = new ThLeaveMovementServicio(ledger, balances);
    ThLeaveRequestServicio service = new ThLeaveRequestServicio(requests, personal, balances, audit, movements);
    ThLeaveRequest request;
    ThLeaveBalance balance;

    @BeforeEach void setup() {
        Personal employee = new Personal(); employee.setIdpersonal(7L);
        request = new ThLeaveRequest();
        request.setIdpersonal_personal(employee); request.setTipolicencia("VACACION");
        request.setFechainicio(LocalDate.of(2026, 10, 10));
        request.setFechafin(LocalDate.of(2026, 10, 12)); request.setUsucrea(8L);
        balance = new ThLeaveBalance(); balance.setEstado(true);
        balance.setDias_asignados(new BigDecimal("10"));
        balance.setDias_disponibles(new BigDecimal("10")); balance.setDias_usados(BigDecimal.ZERO);
        when(personal.findByIdForUpdate(7L)).thenReturn(Optional.of(employee));
        when(requests.save(any())).thenAnswer(i -> i.getArgument(0));
        when(requests.findByIdForUpdate(9L)).thenReturn(Optional.of(request));
        when(balances.findByPersonalAndAnioForUpdate(7L, 2026)).thenReturn(Optional.of(balance));
    }

    @Test void computesCalendarDaysAndNormalizesInitialState() {
        request.setEstado(" solicitada ");
        request.setTipolicencia(" vacacion ");
        ThLeaveRequest saved = service.save(request);
        assertEquals("SOLICITADA", saved.getEstado());
        assertEquals("VACACION", saved.getTipolicencia());
        assertEquals(new BigDecimal("3"), saved.getDias_solicitados());
        assertTrue(saved.getActivo());
        var order = inOrder(personal, requests);
        order.verify(personal).findByIdForUpdate(7L);
        order.verify(requests).existsSolape(7L, request.getFechainicio(), request.getFechafin());
    }

    @Test void approvingHistoricalVacationWithTypeWhitespaceStillDeductsBalance() {
        request.setEstado("SOLICITADA"); request.setTipolicencia(" vacacion ");
        request.setDias_solicitados(new BigDecimal("3"));
        service.aprobar(9L, 8L, "Revisado");
        assertEquals(0, balance.getDias_disponibles().compareTo(new BigDecimal("7")));
        assertEquals("APROBADA", request.getEstado());
        assertEquals("VACACION", request.getTipolicencia());
    }

    @Test void approvingUnknownHistoricalTypeDoesNotBypassBalanceRules() {
        request.setEstado("SOLICITADA"); request.setTipolicencia("DESCONOCIDO");
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, "Revisado")).getStatus().value());
        verify(requests, never()).save(any()); verifyNoInteractions(ledger, audit);
        assertEquals("SOLICITADA", request.getEstado());
    }

    @Test void cannotCreateApprovedOrOverwriteExistingRequest() {
        request.setEstado("APROBADA");
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        request.setEstado(null); request.setIdrequest(9L);
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        verify(requests, never()).save(any());
    }

    @Test void rejectsApprovalMetadataAndInvalidDays() {
        request.setAprobador_id(1L);
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        request.setAprobador_id(null); request.setDias_solicitados(new BigDecimal("-3"));
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        request.setDias_solicitados(new BigDecimal("2"));
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        verify(requests, never()).save(any());
    }

    @Test void rejectsOverlappingCreation() {
        when(requests.existsSolape(7L, request.getFechainicio(), request.getFechafin())).thenReturn(true);
        assertEquals(409, assertThrows(ResponseStatusException.class, () -> service.save(request)).getStatus().value());
        verify(requests, never()).save(any());
    }

    @Test void vacationCannotCrossAnnualBalancesYet() {
        request.setFechainicio(LocalDate.of(2026, 12, 31)); request.setFechafin(LocalDate.of(2027, 1, 2));
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        request.setEstado("SOLICITADA");
        assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, ""));
        verify(balances, never()).save(any());
    }

    @Test void approvalConsumesLockedBalanceOnceAndRecordsActor() {
        request.setEstado("SOLICITADA"); request.setDias_solicitados(new BigDecimal("3"));
        service.aprobar(9L, 8L, "Revisado");
        assertEquals(new BigDecimal("7"), balance.getDias_disponibles());
        assertEquals(new BigDecimal("3"), balance.getDias_usados());
        assertEquals(8L, request.getAprobador_id());
        assertEquals("APROBADA", request.getEstado());
        assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, "Reintento"));
        verify(balances, times(1)).save(balance);
        var order = inOrder(requests, balances);
        order.verify(requests).findByIdForUpdate(9L);
        order.verify(balances).findByPersonalAndAnioForUpdate(7L, 2026);
    }

    @Test void insufficientOrInactiveBalanceDoesNotApprove() {
        request.setEstado("SOLICITADA"); request.setDias_solicitados(new BigDecimal("3"));
        balance.setDias_disponibles(BigDecimal.ONE);
        assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, ""));
        balance.setDias_disponibles(BigDecimal.TEN); balance.setEstado(false);
        assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, ""));
        assertEquals("SOLICITADA", request.getEstado());
        verify(balances, never()).save(any());
    }

    @Test void legacyInconsistentDaysRequireReview() {
        request.setEstado("SOLICITADA"); request.setDias_solicitados(new BigDecimal("-3"));
        assertThrows(ResponseStatusException.class, () -> service.aprobar(9L, 8L, ""));
        verify(balances, never()).save(any());
    }

    @Test void permissionDoesNotConsumeVacationBalance() {
        request.setEstado("SOLICITADA"); request.setTipolicencia("PERMISO");
        service.aprobar(9L, 8L, "");
        assertEquals("APROBADA", request.getEstado());
        verifyNoInteractions(balances);
    }

    @Test void rejectionRequiresReasonAndNeverConsumesBalance() {
        request.setEstado("SOLICITADA");
        assertThrows(ResponseStatusException.class, () -> service.rechazar(9L, 8L, " "));
        service.rechazar(9L, 8L, "Sin justificación");
        assertEquals("RECHAZADA", request.getEstado());
        verifyNoInteractions(balances);
    }

    @Test void cancellationPreservesRequestAndRecordsResolutionWithoutTouchingBalance() {
        request.setIdrequest(9L); request.setEstado("SOLICITADA");
        service.cancelar(9L, 8L, " Cambio de fechas ");
        assertEquals("CANCELADA", request.getEstado());
        assertEquals(8L, request.getResuelto_por());
        assertEquals("Cambio de fechas", request.getMotivo_resolucion());
        assertNotNull(request.getFecha_resolucion());
        assertThrows(ResponseStatusException.class, () -> service.cancelar(9L, 8L, "Reintento"));
        verifyNoInteractions(balances, ledger);
    }

    @Test void withdrawalRequiresReasonAndCompatibleState() {
        request.setEstado("APROBADA");
        assertThrows(ResponseStatusException.class, () -> service.cancelar(9L, 8L, "Cambio"));
        assertThrows(ResponseStatusException.class, () -> service.revertir(9L, 8L, " "));
        request.setEstado("RECHAZADA");
        assertThrows(ResponseStatusException.class, () -> service.revertir(9L, 8L, "Cambio"));
        verifyNoInteractions(balances, ledger);
    }

    @Test void permissionReversalPreservesApprovalAndDoesNotRefundVacationDays() {
        request.setIdrequest(9L); request.setEstado("APROBADA"); request.setTipolicencia("PERMISO");
        request.setAprobador_id(3L); request.setObservacion_aprobacion("Original");
        service.revertir(9L, 8L, "No ejecutado");
        assertEquals("REVERTIDA", request.getEstado());
        assertEquals(3L, request.getAprobador_id());
        assertEquals("Original", request.getObservacion_aprobacion());
        verifyNoInteractions(balances, ledger);
    }

    @Test void creationCannotPrepopulateResolutionFields() {
        request.setResuelto_por(1L);
        assertThrows(ResponseStatusException.class, () -> service.save(request));
        verify(requests, never()).save(any());
    }
}
