package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.interfaces.SuspensionCuentaResumen;
import com.epmapat.erp_epmapat.modelo.SuspensionesM;
import com.epmapat.erp_epmapat.repositorio.AboxSuspensionR;

class SuspensionesCuentasConsultaTest {
    @Test void agregaLasCuentasACadaRegistroConUnaSolaConsultaYCuentaVinculosReales() {
        AboxSuspensionR repo = mock(AboxSuspensionR.class);
        SuspensionesS servicio = new SuspensionesS();
        ReflectionTestUtils.setField(servicio, "relaciones", repo);
        SuspensionesM uno = new SuspensionesM(); uno.setIdsuspension(1L); uno.setTotal(999L);
        SuspensionesM dos = new SuspensionesM(); dos.setIdsuspension(2L); dos.setTipo(1L);
        var filas = List.of(fila(1L, 100L), fila(1L, 200L), fila(1L, 100L), fila(2L, 300L));
        when(repo.findCuentasPorSuspensiones(List.of(1L, 2L))).thenReturn(filas);
        servicio.conCuentas(List.of(uno, dos));
        assertEquals(List.of(100L, 200L), uno.getCuentasAfectadas());
        assertEquals(2, uno.getTotalCuentas());
        assertEquals(List.of(300L), dos.getCuentasAfectadas());
        verify(repo, times(1)).findCuentasPorSuspensiones(List.of(1L, 2L));
    }

    @Test void conservaRegistrosSinCuentasYNoConsultaListadosVacios() {
        AboxSuspensionR repo = mock(AboxSuspensionR.class);
        SuspensionesS servicio = new SuspensionesS();
        ReflectionTestUtils.setField(servicio, "relaciones", repo);
        servicio.conCuentas(List.of());
        verifyNoInteractions(repo);
        SuspensionesM registro = new SuspensionesM(); registro.setIdsuspension(10L);
        when(repo.findCuentasPorSuspensiones(List.of(10L))).thenReturn(List.of());
        assertEquals(1, servicio.conCuentas(List.of(registro)).size());
        assertEquals(0, registro.getTotalCuentas());
        assertTrue(registro.getCuentasAfectadas().isEmpty());
    }

    @Test void publicaCuentasAfectadasEnJsonConLaConfiguracionHibernateDelSistema() throws Exception {
        var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
        mapper.registerModule(new com.epmapat.erp_epmapat.config.JacksonHibernateConfig().hibernate5Module());
        SuspensionesM registro = new SuspensionesM(); registro.setIdsuspension(1L);
        registro.setCuentasAfectadas(List.of(100L, 200L));
        var json = mapper.readTree(mapper.writeValueAsString(registro));
        assertTrue(json.has("cuentasAfectadas"), "La lista calculada debe llegar al frontend, no solo totalCuentas");
        assertEquals(100L, json.get("cuentasAfectadas").get(0).asLong());
        assertEquals(2, json.get("totalCuentas").asInt());
        registro.setCuentasAfectadas(List.of());
        var vacio = mapper.readTree(mapper.writeValueAsString(registro));
        assertTrue(vacio.get("cuentasAfectadas").isArray());
        assertEquals(0, vacio.get("cuentasAfectadas").size());
    }

    private SuspensionCuentaResumen fila(Long id, Long cuenta) {
        SuspensionCuentaResumen fila = mock(SuspensionCuentaResumen.class);
        when(fila.getIdsuspension()).thenReturn(id); when(fila.getCuenta()).thenReturn(cuenta);
        return fila;
    }
}
