package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.RetiroMedidoresRequest;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;

class RetiroMedidoresServicioTest {
    SuspensionesS suspensiones = mock(SuspensionesS.class);
    AbonadosR abonados = mock(AbonadosR.class);
    AboxSuspensionR relaciones = mock(AboxSuspensionR.class);
    AbonadoServicio auditoria = mock(AbonadoServicio.class);
    RetiroMedidoresServicio servicio = new RetiroMedidoresServicio(suspensiones, abonados, relaciones, auditoria);
    SuspensionesM origen = new SuspensionesM();
    Abonados cuenta = new Abonados();
    void preparar() {
        origen.setIdsuspension(10L); origen.setTipo(2L); origen.setNumero(50L);
        cuenta.setIdabonado(123L); cuenta.setEstado(2L);
        AboxSuspensionM vinculo = new AboxSuspensionM(); vinculo.setIdabonado_abonados(cuenta);
        when(suspensiones.findById(10L)).thenReturn(Optional.of(origen));
        when(relaciones.findByIdsuspension(10L)).thenReturn(List.of(vinculo));
        when(abonados.lockParaRetiro(List.of(123L))).thenReturn(List.of(cuenta));
    }
    @Test void creaHistorialConOrigenCuentasYActorYActualizaConAuditoria() {
        preparar();
        when(suspensiones.save(any(SuspensionesM.class))).thenAnswer(i -> {
            SuspensionesM r = i.getArgument(0); r.setIdsuspension(11L); r.setNumero(51L); return r;
        });
        when(auditoria.retirarMedidorConAuditoria(eq(123L), eq(7L), anyString())).thenReturn(cuenta);
        SuspensionesM r = servicio.retirar(10L, new RetiroMedidoresRequest(List.of(123L,123L),7L,"Prueba"),7L);
        assertEquals(3L,r.getTipo()); assertSame(origen,r.getIdsuspension_origen());
        assertEquals(7L,r.getUsucrea()); assertEquals(1L,r.getTotal());
        assertEquals(List.of(123L),r.getCuentasAfectadas());
        verify(auditoria).retirarMedidorConAuditoria(eq(123L),eq(7L),contains("#50"));
        var captor = org.mockito.ArgumentCaptor.forClass(AboxSuspensionM.class);
        verify(relaciones).save(captor.capture());
        assertSame(r,captor.getValue().getIdsuspension_suspensiones());
        assertSame(cuenta,captor.getValue().getIdabonado_abonados());
    }
    @Test void rechazaCuentaAjenaSinGuardarNada() {
        preparar();
        Abonados ajena = new Abonados(); ajena.setIdabonado(999L); ajena.setEstado(2L);
        when(abonados.lockParaRetiro(List.of(999L))).thenReturn(List.of(ajena));
        assertThrows(ResponseStatusException.class, () -> servicio.retirar(10L,new RetiroMedidoresRequest(List.of(999L),7L,null),7L));
        verify(suspensiones,never()).save(any()); verifyNoInteractions(auditoria);

    }
    @Test void rechazaRetiroDuplicadoAntesDeCrearRegistro() {
        preparar(); cuenta.setEstado(3L);
        assertThrows(ResponseStatusException.class, () -> servicio.retirar(10L,new RetiroMedidoresRequest(List.of(123L),7L,null),7L));
        verify(suspensiones,never()).save(any()); verifyNoInteractions(auditoria);
    }
    @Test void propagaFalloAuditoriaParaRevertirTransaccion() {
        preparar(); when(suspensiones.save(any(SuspensionesM.class))).thenAnswer(i -> i.getArgument(0));
        when(auditoria.retirarMedidorConAuditoria(anyLong(),anyLong(),anyString())).thenThrow(new IllegalStateException("Auditoria"));
        assertThrows(IllegalStateException.class, () -> servicio.retirar(10L,new RetiroMedidoresRequest(List.of(123L),7L,null),7L));
        verify(relaciones,never()).save(any());
    }
}
