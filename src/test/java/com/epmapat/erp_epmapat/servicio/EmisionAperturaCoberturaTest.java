package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.PlatformTransactionManager;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;

class EmisionAperturaCoberturaTest {
    EmisionesR emisiones = mock(EmisionesR.class);
    RutasR rutas = mock(RutasR.class);
    RutasxemisionR rutasEmision = mock(RutasxemisionR.class);
    AbonadosR abonados = mock(AbonadosR.class);
    LecturasR lecturas = mock(LecturasR.class);
    FacturasR facturas = mock(FacturasR.class);
    PlatformTransactionManager transactionManager = mock(PlatformTransactionManager.class);
    EmisionGeneracionServicio servicio;

    @BeforeEach void preparar() {
        servicio = new EmisionGeneracionServicio(emisiones, rutas, rutasEmision, abonados, lecturas,
            facturas, mock(RubroxfacR.class), transactionManager);
        Emisiones emision = new Emisiones(); emision.setIdemision(5L); emision.setEmision("2610");
        when(emisiones.findById(5L)).thenReturn(Optional.of(emision));
        Rutas ruta = new Rutas(); ruta.setIdruta(1L); ruta.setEstado(false);
        when(rutas.findRutasParaApertura()).thenReturn(List.of(ruta));
        Rutasxemision re = new Rutasxemision(); re.setIdrutaxemision(10L);
        when(rutasEmision.findOptionalByEmisionRuta(5L, 1L)).thenReturn(Optional.of(re));
        when(abonados.findCuentasSinRutaParaApertura()).thenReturn(List.of());
    }

    @Test void validaCadaCuentaEsperadaYNoCompensaFaltantesConLecturasAjenas() {
        Abonados activa = new Abonados(); activa.setIdabonado(1L); activa.setEstado(1L);
        Abonados suspendida = new Abonados(); suspendida.setIdabonado(2L); suspendida.setEstado(2L);
        when(abonados.findParaAperturaByRuta(1L)).thenReturn(List.of(activa, suspendida));
        when(lecturas.findFirstByIdemisionAndIdabonado(5L, 1L)).thenReturn(Optional.of(new Lecturas()));
        when(lecturas.findFirstByIdemisionAndIdabonado(5L, 2L)).thenReturn(Optional.empty());
        when(abonados.findCuentasPendientesApertura(5L)).thenReturn(List.of(2L));
        var resultado = servicio.validarApertura(5L);
        assertFalse(resultado.isAperturaCompleta());
        assertEquals(2, resultado.getTotalLecturasEsperadas());
        assertEquals(1, resultado.getLecturasExistentes());
        assertEquals(List.of(2L), resultado.getCuentasPendientes());
        assertEquals(1, resultado.getRutas().get(0).getLecturasPendientes());
        verify(lecturas, never()).save(any());
    }

    @Test void generaLecturaYFacturaParaCuentaSuspendidaConEstadoDos() {
        when(transactionManager.getTransaction(any())).thenReturn(new org.springframework.transaction.support.SimpleTransactionStatus());
        Abonados cuenta = new Abonados(); cuenta.setIdabonado(3L); cuenta.setEstado(2L);
        Clientes responsable = new Clientes(); responsable.setIdcliente(7L); cuenta.setIdresponsable(responsable);
        Categorias categoria = new Categorias(); categoria.setIdcategoria(1L); cuenta.setIdcategoria_categorias(categoria);
        when(abonados.findParaAperturaByRuta(1L)).thenReturn(List.of(cuenta));
        when(lecturas.findFirstByIdemisionAndIdabonado(5L, 3L)).thenReturn(Optional.empty());
        when(facturas.save(any())).thenAnswer(inv -> { Facturas f = inv.getArgument(0); f.setIdfactura(20L); return f; });
        when(abonados.findCuentasPendientesApertura(5L)).thenReturn(List.of());
        var resultado = servicio.generarPendientes(5L, 1L);
        assertTrue(resultado.isAperturaCompleta());
        assertEquals(1, resultado.getLecturasCreadas());
        var captura = org.mockito.ArgumentCaptor.forClass(Lecturas.class);
        verify(lecturas).save(captura.capture());
        assertEquals(3L, captura.getValue().getIdabonado_abonados().getIdabonado());
        assertEquals(20L, captura.getValue().getIdfactura());
        assertEquals(5L, captura.getValue().getIdemision());
    }

    @Test void cuentasSinRutaImpidenMarcarLaAperturaComoCompleta() {
        when(abonados.findParaAperturaByRuta(1L)).thenReturn(List.of());
        when(abonados.findCuentasSinRutaParaApertura()).thenReturn(List.of(99L));
        when(abonados.findCuentasPendientesApertura(5L)).thenReturn(List.of(99L));
        var resultado = servicio.validarApertura(5L);
        assertFalse(resultado.isAperturaCompleta());
        assertEquals(List.of(99L), resultado.getCuentasSinRuta());
        assertEquals(1, resultado.getLecturasPendientes());
    }
}
