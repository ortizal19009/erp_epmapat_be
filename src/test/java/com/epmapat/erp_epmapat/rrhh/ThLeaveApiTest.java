package com.epmapat.erp_epmapat.rrhh;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThLeaveApi;
import com.epmapat.erp_epmapat.rrhh.exception.ThLeaveApiExceptionHandler;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThLeaveApiTest {
    ThLeaveAccessService access = mock(ThLeaveAccessService.class);
    ThLeaveRequestServicio requests = mock(ThLeaveRequestServicio.class);
    ThLeaveBalanceServicio balances = mock(ThLeaveBalanceServicio.class);
    ThLeaveMovementServicio movements = mock(ThLeaveMovementServicio.class);
    ThLeaveReconciliationServicio reconciliation = mock(ThLeaveReconciliationServicio.class);
    ThLeaveInboxServicio inbox = mock(ThLeaveInboxServicio.class);
    ThLeaveHistoryServicio history = mock(ThLeaveHistoryServicio.class);
    ThLeaveBalanceStateServicio balanceState = mock(ThLeaveBalanceStateServicio.class);
    MockMvc mvc;

    @BeforeEach void setup() {
        mvc = MockMvcBuilders.standaloneSetup(new ThLeaveApi(access, balances, requests, movements, reconciliation, inbox, history, balanceState))
                .setControllerAdvice(new ThLeaveApiExceptionHandler()).build();
        when(access.require(any(), anyBoolean())).thenReturn(8L);
    }

    @Test void adjustmentUsesSessionActor() throws Exception {
        mvc.perform(post("/api/th-leave/balances/3/ajustar").contentType(MediaType.APPLICATION_JSON)
            .content("{\"dias\":1,\"motivo\":\"Revision\",\"clave\":\"00000000-0000-0000-0000-000000000001\",\"usuario\":1}"))
            .andExpect(status().isNoContent());
        verify(movements).ajustar(3L, java.math.BigDecimal.ONE, "Revision", "00000000-0000-0000-0000-000000000001", 8L);
    }

    @Test void bookExportUsesReadPermissionAndSelectedYear() throws Exception {
        when(movements.byPersonal(7L, 2026)).thenReturn(java.util.List.of());
        mvc.perform(get("/api/th-leave/movements/persona/7/libro.csv").param("anio", "2026"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"libro-rrhh-7-2026.csv\""));
        verify(access).require(any(), eq(false)); verify(movements).byPersonal(7L, 2026);
    }

    @Test void bookExportRejectsMissingYearAndInvalidYearOrPerson() throws Exception {
        mvc.perform(get("/api/th-leave/movements/persona/7/libro.csv")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/th-leave/movements/persona/7/libro.csv").param("anio", "1899")).andExpect(status().isBadRequest());
        mvc.perform(get("/api/th-leave/movements/persona/0/libro.csv").param("anio", "2026")).andExpect(status().isBadRequest());
        verifyNoInteractions(movements);
    }

    @Test void deniedReadCannotExportBook() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/movements/persona/7/libro.csv").param("anio", "2026")).andExpect(status().isForbidden());
        verifyNoInteractions(movements);
    }

    @Test void calendarUsesReadPermissionAndRequiresBothDates() throws Exception {
        when(inbox.calendario("PERMISO", 7L, java.time.LocalDate.parse("2026-10-01"), java.time.LocalDate.parse("2026-10-31"), 0))
                .thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse(java.util.List.of(), 0, 100, 0, 0));
        mvc.perform(get("/api/th-leave/requests/calendario").accept(MediaType.APPLICATION_JSON).param("tipo", "PERMISO").param("idpersonal", "7")
                .param("desde", "2026-10-01").param("hasta", "2026-10-31"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.tamano").value(100));
        verify(access).require(any(), eq(false));
        mvc.perform(get("/api/th-leave/requests/calendario").param("desde", "2026-10-01"))
                .andExpect(status().isBadRequest());
    }

    @Test void deniedReadCannotViewCalendar() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/requests/calendario").param("desde", "2026-10-01").param("hasta", "2026-10-31"))
                .andExpect(status().isForbidden());
        verifyNoInteractions(inbox);
    }

    @Test void inboxCsvUsesReadPermissionAndSameBoundedFilters() throws Exception {
        when(inbox.consultar("APROBADA", "VACACION", 7L, java.time.LocalDate.parse("2026-10-01"), null, 2, 10))
                .thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse(java.util.List.of(), 2, 10, 0, 0));
        mvc.perform(get("/api/th-leave/requests/bandeja.csv").param("estado", "APROBADA").param("tipo", "VACACION")
                .param("idpersonal", "7").param("desde", "2026-10-01").param("pagina", "2").param("tamano", "10"))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(header().string("Content-Type", "text/csv; charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"solicitudes-rrhh-pagina-3.csv\""));
        verify(access).require(any(), eq(false));
        verify(inbox).consultar("APROBADA", "VACACION", 7L, java.time.LocalDate.parse("2026-10-01"), null, 2, 10);
    }

    @Test void deniedReadCannotExportInbox() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/requests/bandeja.csv")).andExpect(status().isForbidden());
        verifyNoInteractions(inbox);
    }

    @Test void changingBalanceStateUsesTokenActorAndConsultedVersion() throws Exception {
        mvc.perform(post("/api/th-leave/balances/3/estado").contentType(MediaType.APPLICATION_JSON)
                .content("{\"activo\":false,\"version\":4,\"motivo\":\"Revisar\",\"usuario\":1}"))
                .andExpect(status().isNoContent());
        verify(balanceState).cambiar(3L, false, 4L, "Revisar", 8L);
    }

    @Test void readOnlyCanViewBalanceStateHistoryButCannotChangeIt() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        when(balanceState.historial(3L)).thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveBalanceStateResponse(
                3L, 7L, 2026, false, 4L, java.util.List.of()));
        mvc.perform(get("/api/th-leave/balances/3/historial-estado").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.version").value(4)).andExpect(jsonPath("$.estado").value(false));
        mvc.perform(post("/api/th-leave/balances/3/estado").contentType(MediaType.APPLICATION_JSON).content("{}"))
                .andExpect(status().isForbidden());
        verify(balanceState).historial(3L); verifyNoMoreInteractions(balanceState);
    }

    @Test void deniedReadCannotViewBalanceStateHistory() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/balances/3/historial-estado")).andExpect(status().isForbidden());
        verifyNoInteractions(balanceState);
    }

    @Test void historyAllowsReadOnlyAccessAndReturnsLimitedRequestData() throws Exception {
        var personal = new com.epmapat.erp_epmapat.rrhh.modelo.Personal(); personal.setIdpersonal(7L);
        personal.setEmail("privado@example.test");
        var r = new com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest(); r.setIdrequest(9L); r.setIdpersonal_personal(personal);
        var dto = new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveHistoryResponse(
                com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse.Solicitud.from(r), java.time.LocalDateTime.now(),
                java.util.List.of(), java.util.List.of(), java.util.List.of("Sin evento de creación"));
        when(history.consultar(9L)).thenReturn(dto);
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/requests/9/historial").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.solicitud.idrequest").value(9)).andExpect(jsonPath("$.eventos").isEmpty())
                .andExpect(jsonPath("$.advertencias[0]").value("Sin evento de creación"))
                .andExpect(jsonPath("$.solicitud.email").doesNotExist()).andExpect(jsonPath("$.solicitud.idpersonal_personal").doesNotExist());
        verify(access).require(any(), eq(false)); verify(history).consultar(9L);
    }

    @Test void deniedReadCannotObtainHistory() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/requests/9/historial")).andExpect(status().isForbidden());
        verifyNoInteractions(history);
    }

    @Test void missingHistoryRequestReturnsNotFound() throws Exception {
        when(history.consultar(999L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Solicitud no encontrada"));
        mvc.perform(get("/api/th-leave/requests/999/historial").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound()).andExpect(jsonPath("$.message").value("Solicitud no encontrada"));
    }

    @Test void inboxDefaultsToPendingAndAllowsReadOnlyAccess() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        when(inbox.consultar("SOLICITADA", null, null, null, null, 0, 20))
                .thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse(java.util.List.of(), 0, 20, 0, 0));
        mvc.perform(get("/api/th-leave/requests/bandeja").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(jsonPath("$.contenido").isEmpty())
                .andExpect(jsonPath("$.tamano").value(20)).andExpect(header().string("Cache-Control", "no-store"));
        verify(access).require(any(), eq(false));
        verify(inbox).consultar("SOLICITADA", null, null, null, null, 0, 20);
    }

    @Test void inboxPassesExplicitFiltersAndDoesNotExposeWholePersonalRecord() throws Exception {
        var p = new com.epmapat.erp_epmapat.rrhh.modelo.Personal(); p.setIdpersonal(7L); p.setNombres("Prueba");
        p.setEmail("privado@example.test"); p.setDireccion("Privada");
        var r = new com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest(); r.setIdrequest(9L); r.setIdpersonal_personal(p);
        r.setEstado("APROBADA"); r.setTipolicencia("PERMISO");
        when(inbox.consultar("APROBADA", "PERMISO", 7L, java.time.LocalDate.of(2026, 10, 1), java.time.LocalDate.of(2026, 10, 31), 2, 10))
                .thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse(
                        java.util.List.of(com.epmapat.erp_epmapat.rrhh.dto.ThLeaveInboxResponse.Solicitud.from(r)), 2, 10, 21, 3));
        mvc.perform(get("/api/th-leave/requests/bandeja?estado=APROBADA&tipo=PERMISO&idpersonal=7&desde=2026-10-01&hasta=2026-10-31&pagina=2&tamano=10")
                .accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
                .andExpect(jsonPath("$.contenido[0].idpersonal").value(7)).andExpect(jsonPath("$.contenido[0].nombres").value("Prueba"))
                .andExpect(jsonPath("$.contenido[0].email").doesNotExist()).andExpect(jsonPath("$.contenido[0].idpersonal_personal").doesNotExist());
    }

    @Test void deniedReadCannotObtainInbox() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/requests/bandeja")).andExpect(status().isForbidden());
        verifyNoInteractions(inbox);
    }

    @Test void malformedInboxDateIsRejectedBeforeQuerying() throws Exception {
        mvc.perform(get("/api/th-leave/requests/bandeja?desde=ayer")).andExpect(status().isBadRequest());
        verifyNoInteractions(inbox);
    }

    @Test void reconciliationAllowsReadOnlyAccessAndPassesYear() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        when(reconciliation.verificar(7L, 2026)).thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationResponse(
                7L, java.time.LocalDateTime.of(2026, 10, 6, 10, 0), java.util.List.of()));
        mvc.perform(get("/api/th-leave/balances/persona/7/conciliacion?anio=2026").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()).andExpect(header().string("Cache-Control", "no-store"))
                .andExpect(jsonPath("$.idpersonal").value(7)).andExpect(jsonPath("$.ejercicios").isEmpty());
        verify(access).require(any(), eq(false)); verify(reconciliation).verificar(7L, 2026);
        verifyNoInteractions(balances, requests, movements);
    }

    @Test void reconciliationCsvUsesAuthorizedReportWithDownloadHeaders() throws Exception {
        when(reconciliation.verificar(7L, null)).thenReturn(new com.epmapat.erp_epmapat.rrhh.dto.ThLeaveReconciliationResponse(
                7L, java.time.LocalDateTime.of(2026, 10, 6, 10, 0), java.util.List.of()));
        mvc.perform(get("/api/th-leave/balances/persona/7/conciliacion.csv"))
                .andExpect(status().isOk()).andExpect(content().contentType("text/csv;charset=UTF-8"))
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"conciliacion-rrhh-7.csv\""))
                .andExpect(header().string("Cache-Control", "no-store"));
        verify(access).require(any(), eq(false)); verify(reconciliation).verificar(7L, null);
    }

    @Test void deniedReadCannotObtainReportOrCsv() throws Exception {
        when(access.require(any(), eq(false))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/balances/persona/7/conciliacion")).andExpect(status().isForbidden());
        mvc.perform(get("/api/th-leave/balances/persona/7/conciliacion.csv")).andExpect(status().isForbidden());
        verifyNoInteractions(reconciliation);
    }

    @Test void readOnlyCannotAdjustOrOpenBook() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(post("/api/th-leave/balances/3/ajustar").contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden());
        mvc.perform(post("/api/th-leave/balances/3/abrir-libro")).andExpect(status().isForbidden());
        verifyNoInteractions(movements);
    }

    @Test void approvalUsesAuthenticatedActorEvenWithForgedBody() throws Exception {
        mvc.perform(post("/api/th-leave/requests/9/aprobar").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content("{\"aprobadorId\":1,\"observacion\":\"Revisado\"}"))
            .andExpect(status().isOk());
        verify(requests).aprobar(9L, 8L, "Revisado");
    }

    @Test void creationReplacesForgedCreator() throws Exception {
        mvc.perform(post("/api/th-leave/requests").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON)
            .content("{\"usucrea\":1}"))
            .andExpect(status().isOk());
        verify(requests).save(argThat(r -> r.getUsucrea().equals(8L)));
    }

    @Test void deniedWriteNeverInvokesBusinessService() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permiso"));
        mvc.perform(post("/api/th-leave/requests/9/aprobar").accept(MediaType.APPLICATION_JSON).contentType(MediaType.APPLICATION_JSON).content("{}"))
            .andExpect(status().isForbidden()).andExpect(jsonPath("$.message").value("Sin permiso"));
        verifyNoInteractions(requests);
    }

    @Test void permissionEndpointReportsReadOnly() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN));
        mvc.perform(get("/api/th-leave/permissions").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(8)).andExpect(jsonPath("$.canWrite").value(false));
    }

    @Test void reversalUsesTokenActorAndBusinessReason() throws Exception {
        mvc.perform(post("/api/th-leave/requests/9/revertir").accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Corrección\",\"actor\":1}"))
                .andExpect(status().isOk());
        verify(requests).revertir(9L, 8L, "Corrección");
    }

    @Test void readOnlyUserCannotCancelOrReverse() throws Exception {
        when(access.require(any(), eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permiso"));
        for (String action : new String[] {"cancelar", "revertir"}) {
            mvc.perform(post("/api/th-leave/requests/9/" + action).accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON).content("{\"motivo\":\"Corrección\"}"))
                    .andExpect(status().isForbidden());
        }
        verifyNoInteractions(requests, movements);
    }

    @Test void movementHistoryChecksReadAccessAndPassesYearFilter() throws Exception {
        mvc.perform(get("/api/th-leave/movements/persona/7?anio=2026").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(access).require(any(), eq(false));
        verify(movements).byPersonal(7L, 2026);
    }
}
