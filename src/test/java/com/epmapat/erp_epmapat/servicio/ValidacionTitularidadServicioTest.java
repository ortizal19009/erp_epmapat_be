package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.repositorio.*;

class ValidacionTitularidadServicioTest {
    private final AbonadosR abonados = mock(AbonadosR.class);
    private final FacturasR facturas = mock(FacturasR.class);
    private final ConveniosR convenios = mock(ConveniosR.class);
    private final ValidacionTitularidadServicio servicio =
        new ValidacionTitularidadServicio(abonados, facturas, convenios);

    @BeforeEach void setup() { when(abonados.existsById(123L)).thenReturn(true); }

    @Test void permiteCuentaSinDeudasAunqueTengaFacturacionFutura() {
        assertDoesNotThrow(() -> servicio.validar(123L));
    }

    @Test void bloqueaFacturaExistentePendiente() {
        when(facturas.contarDeudasParaCambioTitularidad(123L)).thenReturn(1L);
        assertThrows(ResponseStatusException.class, () -> servicio.validar(123L));
    }

    @Test void bloqueaConvenioActivoAunqueNoAparezcaEnListadoDeFacturas() {
        when(convenios.contarConveniosActivosParaTitularidad(123L)).thenReturn(1L);
        assertThrows(ResponseStatusException.class, () -> servicio.validar(123L));
    }

    @Test void exigeCuentaValida() {
        assertThrows(ResponseStatusException.class, () -> servicio.validar(null));
        assertThrows(ResponseStatusException.class, () -> servicio.validar(999L));
        verifyNoInteractions(facturas, convenios);
    }

    @Test void guardarTramiteValidaAmbosTiposAntesDePersistir() {
        AguaTramiteR dao = mock(AguaTramiteR.class);
        AguaTramiteServicio tramites = new AguaTramiteServicio();
        org.springframework.test.util.ReflectionTestUtils.setField(tramites, "dao", dao);
        org.springframework.test.util.ReflectionTestUtils.setField(tramites, "abonadosR", abonados);
        org.springframework.test.util.ReflectionTestUtils.setField(tramites, "validacionTitularidad", servicio);
        when(facturas.contarDeudasParaCambioTitularidad(123L)).thenReturn(1L);
        for (long idtipo : new long[] {5L, 10L}) {
            com.epmapat.erp_epmapat.modelo.AguaTramite tramite = new com.epmapat.erp_epmapat.modelo.AguaTramite();
            com.epmapat.erp_epmapat.modelo.TipoTramite tipo = new com.epmapat.erp_epmapat.modelo.TipoTramite();
            tipo.setIdtipotramite(idtipo);
            tramite.setIdtipotramite_tipotramite(tipo);
            tramite.setIdabonado(123L);
            assertThrows(ResponseStatusException.class, () -> tramites.save(tramite));
        }
        verifyNoInteractions(dao);
    }
}
