package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.AguaTramite;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.repositorio.AguaTramiteR;

class AguaTramiteCuentaTest {
    AguaTramiteR dao = mock(AguaTramiteR.class);
    AbonadosR abonados = mock(AbonadosR.class);
    AguaTramiteServicio servicio = new AguaTramiteServicio();
    @BeforeEach void preparar() {
        ReflectionTestUtils.setField(servicio, "dao", dao);
        ReflectionTestUtils.setField(servicio, "abonadosR", abonados);
    }
    @Test void guardaElIdabonadoSinSustituirloPorElMedidor() {
        AguaTramite tramite = new AguaTramite(); tramite.setIdabonado(123L); tramite.setCodmedidor("987654");
        when(abonados.existsById(123L)).thenReturn(true);
        when(dao.save(tramite)).thenReturn(tramite);
        assertEquals(123L, servicio.save(tramite).getIdabonado());
        verify(dao).save(tramite);
    }
    @Test void rechazaUnaCuentaQueNoExisteEnAbonados() {
        AguaTramite tramite = new AguaTramite(); tramite.setIdabonado(987654L);
        assertThrows(ResponseStatusException.class, () -> servicio.save(tramite));
        verifyNoInteractions(dao);
    }
    @Test void permiteIngresarLaSolicitudAntesDeCrearLaNuevaCuenta() {
        AguaTramite tramite = new AguaTramite();
        when(dao.save(tramite)).thenReturn(tramite);
        assertNull(servicio.save(tramite).getIdabonado());
        verifyNoInteractions(abonados);
    }
}
