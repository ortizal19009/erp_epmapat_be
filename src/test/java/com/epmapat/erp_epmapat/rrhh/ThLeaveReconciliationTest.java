package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.dto.*;
import com.epmapat.erp_epmapat.rrhh.modelo.*;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement.Tipo;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveReconciliationServicio;

class ThLeaveReconciliationTest {
    PersonalR personal = mock(PersonalR.class);
    ThLeaveBalanceR balances = mock(ThLeaveBalanceR.class);
    ThLeaveMovementR movements = mock(ThLeaveMovementR.class);
    ThLeaveRequestR requests = mock(ThLeaveRequestR.class);
    ThLeaveReconciliationServicio service = new ThLeaveReconciliationServicio(personal, balances, movements, requests);
    ThLeaveBalance balance;

    BigDecimal n(String value) { return new BigDecimal(value); }

    @BeforeEach void setup() {
        Personal p = new Personal(); p.setIdpersonal(7L);
        balance = new ThLeaveBalance(); balance.setIdbalance(3L); balance.setIdpersonal_personal(p);
        balance.setAnio(2026); balance.setEstado(true); balance.setDias_asignados(n("5"));
        balance.setDias_usados(n("2")); balance.setDias_disponibles(n("3"));
        when(personal.existsById(7L)).thenReturn(true);
        when(balances.findByPersonal(7L)).thenReturn(List.of(balance));
        when(movements.findByPersonal(7L, null)).thenReturn(List.of());
        when(requests.findByPersonal(7L)).thenReturn(List.of());
    }

    ThLeaveMovement opening() {
        ThLeaveMovement m = new ThLeaveMovement(); m.setIdmovement(1L); m.setBalance(balance); m.setTipo(Tipo.APERTURA);
        m.setDias(n("3")); m.setDisponibles_antes(n("0")); m.setDisponibles_despues(n("3"));
        m.setUsados_antes(n("2")); m.setUsados_despues(n("2")); m.setAsignados(n("5"));
        return m;
    }

    ThLeaveRequest historical(Long id, String state, String type, Integer year) {
        ThLeaveRequest r = new ThLeaveRequest(); r.setIdrequest(id); r.setEstado(state); r.setTipolicencia(type);
        r.setIdpersonal_personal(balance.getIdpersonal_personal());
        if (year != null) r.setFechainicio(LocalDate.of(year, 10, 1));
        r.setDias_solicitados(BigDecimal.ONE); return r;
    }

    ThLeaveMovement consumption(ThLeaveRequest request) {
        ThLeaveMovement m = new ThLeaveMovement(); m.setIdmovement(2L); m.setBalance(balance); m.setTipo(Tipo.CONSUMO);
        m.setRequest(request); m.setDias(n("-1")); m.setDisponibles_antes(n("3")); m.setDisponibles_despues(n("2"));
        m.setUsados_antes(n("2")); m.setUsados_despues(n("3")); m.setAsignados(n("5"));
        return m;
    }

    @Test void revertedRequestWithoutRefundDoesNotPassEvenIfBalanceMatchesItsDebit() {
        ThLeaveRequest request = historical(9L, "REVERTIDA", "VACACION", 2026);
        when(movements.findByPersonal(7L, null)).thenReturn(List.of(consumption(request), opening()));
        when(requests.findByPersonal(7L)).thenReturn(List.of(request));
        balance.setDias_usados(n("3")); balance.setDias_disponibles(n("2"));
        var row = service.verificar(7L, 2026).getEjercicios().get(0);
        assertEquals("DIFERENCIA", row.getEstado_libro()); assertEquals(0, row.getDiferencia_disponibles().signum());
        assertTrue(row.getAlertas().stream().anyMatch(a -> a.contains("revertida sin reintegro")));
        assertTrue(row.getSolicitudes_sin_consumo().isEmpty());
    }

    @Test void requestBelongingToAnotherPersonOrExerciseIsNotAcceptedAsValidConsumption() {
        ThLeaveRequest request = historical(9L, "APROBADA", "VACACION", 2026);
        Personal other = new Personal(); other.setIdpersonal(8L); request.setIdpersonal_personal(other);
        when(movements.findByPersonal(7L, null)).thenReturn(List.of(consumption(request), opening()));
        balance.setDias_usados(n("3")); balance.setDias_disponibles(n("2"));
        assertEquals("DIFERENCIA", service.verificar(7L, 2026).getEjercicios().get(0).getEstado_libro());
        request.setIdpersonal_personal(balance.getIdpersonal_personal()); request.setFechainicio(LocalDate.of(2025, 10, 1));
        assertEquals("DIFERENCIA", service.verificar(7L, 2026).getEjercicios().get(0).getEstado_libro());
    }

    @Test void noLedgerDoesNotClaimZeroDifferenceOrCertifyExistingUsedDays() {
        var row = service.verificar(7L, null).getEjercicios().get(0);
        assertEquals("SIN_LIBRO", row.getEstado_libro());
        assertNull(row.getDisponibles_libro()); assertNull(row.getDiferencia_disponibles());
        assertEquals(0, row.getUsados_actuales().compareTo(n("2")));
        verifyNoMoreInteractionsAfterReads();
    }

    void verifyNoMoreInteractionsAfterReads() {
        verify(personal).existsById(7L); verify(balances).findByPersonal(7L);
        verify(movements).findByPersonal(7L, null); verify(requests).findByPersonal(7L);
        verifyNoMoreInteractions(personal, balances, movements, requests);
    }

    @Test void matchingOpeningStillFlagsHistoricalApprovalsWithoutInventingConsumptions() {
        when(movements.findByPersonal(7L, null)).thenReturn(List.of(opening()));
        when(requests.findByPersonal(7L)).thenReturn(List.of(historical(9L, "aprobada", "vacacion", 2026),
                historical(10L, "REVERTIDA", "VACACION", 2026), historical(11L, "SOLICITADA", "VACACION", 2026),
                historical(12L, "APROBADA", "PERMISO", 2026)));
        var row = service.verificar(7L, 2026).getEjercicios().get(0);
        assertEquals("COINCIDE", row.getEstado_libro());
        assertEquals(List.of(9L, 10L), row.getSolicitudes_sin_consumo());
        assertEquals(0, row.getConsumos().signum());
        assertEquals(0, row.getUsados_libro().compareTo(n("2")));
        assertFalse(row.getAlertas().isEmpty()); verifyNoMoreInteractionsAfterReads();
    }

    @Test void externallyModifiedBalanceShowsAllThreeSignedDifferences() {
        when(movements.findByPersonal(7L, null)).thenReturn(List.of(opening()));
        balance.setDias_asignados(n("7")); balance.setDias_usados(n("3")); balance.setDias_disponibles(n("4"));
        var row = service.verificar(7L, null).getEjercicios().get(0);
        assertEquals("DIFERENCIA", row.getEstado_libro());
        assertEquals(0, row.getDiferencia_disponibles().compareTo(BigDecimal.ONE));
        assertEquals(0, row.getDiferencia_usados().compareTo(BigDecimal.ONE));
        assertEquals(0, row.getDiferencia_asignados().compareTo(n("2")));
    }

    @Test void corruptedIntermediateSnapshotsAreDetectedEvenIfFinalBalanceAndSumAgree() {
        ThLeaveMovement up = new ThLeaveMovement(); up.setIdmovement(2L); up.setBalance(balance); up.setTipo(Tipo.AJUSTE);
        up.setDias(n("1")); up.setDisponibles_antes(n("2")); up.setDisponibles_despues(n("3"));
        up.setUsados_antes(n("2")); up.setUsados_despues(n("2")); up.setAsignados(n("5"));
        up.setClave("operation1"); up.setMotivo("Corrección");
        ThLeaveMovement down = new ThLeaveMovement(); down.setIdmovement(3L); down.setBalance(balance); down.setTipo(Tipo.AJUSTE);
        down.setDias(n("-1")); down.setDisponibles_antes(n("4")); down.setDisponibles_despues(n("3"));
        down.setUsados_antes(n("2")); down.setUsados_despues(n("2")); down.setAsignados(n("5"));
        down.setClave("operation2"); down.setMotivo("Corrección");
        when(movements.findByPersonal(7L, null)).thenReturn(List.of(down, up, opening()));
        var row = service.verificar(7L, null).getEjercicios().get(0);
        assertEquals(0, row.getDiferencia_disponibles().signum());
        assertEquals("DIFERENCIA", row.getEstado_libro());
        assertTrue(row.getAlertas().stream().anyMatch(a -> a.contains("#2")));
    }

    @Test void nullBalanceValuesAndMissingRequestYearRemainVisible() {
        balance.setDias_asignados(null);
        when(requests.findByPersonal(7L)).thenReturn(List.of(historical(9L, "APROBADA", "VACACION", null)));
        var rows = service.verificar(7L, null).getEjercicios();
        assertEquals(2, rows.size()); assertEquals("SALDO_INVALIDO", rows.get(0).getEstado_libro());
        assertNull(rows.get(1).getAnio()); assertEquals("SIN_SALDO", rows.get(1).getEstado_libro());
        assertEquals(List.of(9L), rows.get(1).getSolicitudes_sin_consumo());
    }

    @Test void duplicateAnnualBalancesAreNotSilentlyCollapsed() {
        ThLeaveBalance duplicate = new ThLeaveBalance(); duplicate.setIdbalance(4L); duplicate.setAnio(2026);
        when(balances.findByPersonal(7L)).thenReturn(List.of(balance, duplicate));
        var row = service.verificar(7L, null).getEjercicios().get(0);
        assertEquals("SALDO_INVALIDO", row.getEstado_libro()); assertNull(row.getIdbalance());
        assertTrue(row.getAlertas().get(0).contains("[3, 4]"));
    }

    @Test void historicalApprovalsWithoutBalanceAndYearFilterAreExplicit() {
        when(requests.findByPersonal(7L)).thenReturn(List.of(historical(9L, "APROBADA", "VACACION", 2025)));
        var report = service.verificar(7L, 2025);
        assertEquals(1, report.getEjercicios().size()); assertEquals(2025, report.getEjercicios().get(0).getAnio());
        assertEquals("SIN_SALDO", report.getEjercicios().get(0).getEstado_libro());
        assertEquals(List.of(9L), report.getEjercicios().get(0).getSolicitudes_sin_consumo());
        assertTrue(service.verificar(7L, 2024).getEjercicios().isEmpty());
    }

    @Test void invalidYearAndUnknownPersonHaveUsefulStatusCodes() {
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.verificar(7L, 0)).getStatus().value());
        assertEquals(400, assertThrows(ResponseStatusException.class, () -> service.verificar(0L, null)).getStatus().value());
        assertEquals(404, assertThrows(ResponseStatusException.class, () -> service.verificar(999L, null)).getStatus().value());
        verifyNoInteractions(balances, movements, requests);
    }

    @Test void csvPreservesUnknownValuesUtf8AndEscapesTextFormulasAndQuotes() {
        var row = new ThLeaveReconciliationResponse.Ejercicio(2026, 3L, false, "SIN_LIBRO", n("5"), n("2"), n("3"),
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, n("-1.25"), null, null, null, null, null, null,
                0, List.of(9L), List.of(" =SUM(1;2)\n\"Revisión\""));
        String csv = new String(ThLeaveReconciliationCsv.render(new ThLeaveReconciliationResponse(7L,
                LocalDateTime.of(2026, 10, 6, 10, 0), List.of(row))), StandardCharsets.UTF_8);
        assertTrue(csv.startsWith("\uFEFFPersonal;"));
        assertTrue(csv.contains(";-1.25;;;;;;;0;"));
        assertTrue(csv.contains("\"' =SUM(1;2)\n\"\"Revisión\"\"\""));
        assertTrue(csv.endsWith("\r\n"));
    }
}
