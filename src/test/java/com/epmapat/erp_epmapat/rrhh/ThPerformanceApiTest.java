package com.epmapat.erp_epmapat.rrhh;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThPerformanceApi;
import com.epmapat.erp_epmapat.rrhh.dto.ThPerformanceRequest;
import com.epmapat.erp_epmapat.rrhh.exception.ThPerformanceApiExceptionHandler;
import com.epmapat.erp_epmapat.rrhh.modelo.ThPerformanceReview;
import com.epmapat.erp_epmapat.rrhh.repositorio.*;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThPerformanceApiTest {
    ThPerformanceAccessService access = mock(ThPerformanceAccessService.class);
    ThPerformanceServicio service = mock(ThPerformanceServicio.class);
    MockMvc mvc;
    String valid = "{\"idpersonal\":7,\"periodo\":\"2026\",\"fecha\":\"2026-10-15\",\"puntaje\":90.25,\"evaluador\":\"Jefatura\",\"observaciones\":\"Cumple objetivos\",\"usuario\":1}";
    @BeforeEach void setup() {
        when(access.require(any(), anyBoolean())).thenReturn(8L);
        mvc = MockMvcBuilders.standaloneSetup(new ThPerformanceApi(access, service))
            .setControllerAdvice(new ThPerformanceApiExceptionHandler()).build();
    }
    @Test void usesAuthenticatedActorAndSelectedEmployee() throws Exception {
        mvc.perform(post("/api/th-performance").contentType(MediaType.APPLICATION_JSON).content(valid)).andExpect(status().isCreated());
        verify(service).registrar(argThat(p -> p.getIdpersonal() == 7L && p.getPuntaje().equals(new BigDecimal("90.25"))),eq(8L));
    }
    @Test void rejectsInvalidScoresAndBlankFields() throws Exception {
        for (String score : new String[]{"-1","101","90.251"})
            mvc.perform(post("/api/th-performance").contentType(MediaType.APPLICATION_JSON).content(valid.replace("90.25",score))).andExpect(status().isBadRequest());
        mvc.perform(post("/api/th-performance").contentType(MediaType.APPLICATION_JSON).content(valid.replace("Jefatura","   "))).andExpect(status().isBadRequest());
        verifyNoInteractions(service);
    }
    @Test void deniesWriteBeforeCallingService() throws Exception {
        when(access.require(any(),eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo consulta"));
        mvc.perform(post("/api/th-performance").contentType(MediaType.APPLICATION_JSON).content(valid)).andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }
    @Test void readChecksPermissionsAndPreservesPagination() throws Exception {
        mvc.perform(get("/api/th-performance/persona/7").param("page","2").param("size","10"))
            .andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store"));
        verify(access).require(any(),eq(false)); verify(service).listar(7L,2,10);
    }
    @Test void persistsActorAndAuditForRealPersonalCatalog() {
        ThPerformanceReviewR repo = mock(ThPerformanceReviewR.class);
        PersonalR personal = mock(PersonalR.class); ThAuditServicio audit = mock(ThAuditServicio.class);
        when(personal.existsById(7L)).thenReturn(true);
        when(repo.saveAndFlush(any())).thenAnswer(i -> { ThPerformanceReview r = i.getArgument(0); r.setIdreview(3L); return r; });
        ThPerformanceRequest body = new ThPerformanceRequest(); body.setIdpersonal(7L); body.setPeriodo(" 2026 ");
        body.setFecha(LocalDate.of(2026,10,15)); body.setPuntaje(new BigDecimal("90.25")); body.setEvaluador(" Jefatura "); body.setObservaciones(" Cumple ");
        var result = new ThPerformanceServicio(repo,personal,audit).registrar(body,8L);
        assertEquals(8L,result.getUsuario()); assertEquals("2026",result.getPeriodo()); assertNotNull(result.getCreado());
        verify(audit).log(eq("PERSONAL"),eq(7L),eq("EVALUACION_REGISTRADA"),contains("#3"),eq(8L));
    }
    @Test void missingPersonalNeverWritesOrAudits() {
        ThPerformanceReviewR repo = mock(ThPerformanceReviewR.class);
        PersonalR personal = mock(PersonalR.class); ThAuditServicio audit = mock(ThAuditServicio.class);
        ThPerformanceRequest body = new ThPerformanceRequest(); body.setIdpersonal(999L);
        assertThrows(ResponseStatusException.class, () -> new ThPerformanceServicio(repo,personal,audit).registrar(body,8L));
        verifyNoInteractions(repo,audit);
    }
}
