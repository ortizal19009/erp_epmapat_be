package com.epmapat.erp_epmapat.controlador;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.epmapat.erp_epmapat.repositorio.AbonadosReporteConsulta;

class AbonadosReportesApiTest {
    private final AbonadosReporteConsulta consulta = mock(AbonadosReporteConsulta.class);
    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new AbonadosReportesApi(consulta)).build();
    private static final String URL = "/abonados/reportes/medidores-antiguos";

    @Test
    void devuelveCantidadYDetalleParaElCorteSolicitado() throws Exception {
        when(consulta.medidoresAntiguos(LocalDate.of(2026, 11, 13)))
                .thenReturn(List.of(Map.of("idabonado", 7L, "acumuladom3", 5100)));
        mvc.perform(get(URL).accept(org.springframework.http.MediaType.APPLICATION_JSON).param("fechaCorte", "2026-11-13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fechaCorte").value("2026-11-13"))
                .andExpect(jsonPath("$.cantidad").value(1))
                .andExpect(jsonPath("$.detalle[0].idabonado").value(7));
    }

    @Test
    void devuelveCeroSinCoincidencias() throws Exception {
        when(consulta.medidoresAntiguos(LocalDate.of(2026, 11, 13))).thenReturn(List.of());
        mvc.perform(get(URL).accept(org.springframework.http.MediaType.APPLICATION_JSON).param("fechaCorte", "2026-11-13"))
                .andExpect(status().isOk()).andExpect(jsonPath("$.cantidad").value(0))
                .andExpect(jsonPath("$.detalle").isEmpty());
    }

    @Test
    void requiereFechaValida() throws Exception {
        mvc.perform(get(URL)).andExpect(status().isBadRequest());
        mvc.perform(get(URL).accept(org.springframework.http.MediaType.APPLICATION_JSON).param("fechaCorte", "2026-02-30")).andExpect(status().isBadRequest());
    }
}

