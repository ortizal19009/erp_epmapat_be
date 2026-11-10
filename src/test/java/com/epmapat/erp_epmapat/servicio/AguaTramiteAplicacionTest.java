package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.AguaTramiteAplicacionRequest;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;

class AguaTramiteAplicacionTest {
    final AguaTramiteServicio tramites = mock(AguaTramiteServicio.class);
    final AbonadosR abonados = mock(AbonadosR.class);
    final AuditoriaGenericaService auditoria = mock(AuditoriaGenericaService.class);
    final EntityManager em = mock(EntityManager.class);
    final AguaTramiteAplicacionServicio servicio = new AguaTramiteAplicacionServicio(tramites, abonados, auditoria, em);

    AguaTramiteAplicacionRequest solicitud(long tipo, long estado) {
        var request = new AguaTramiteAplicacionRequest();
        var tramite = new AguaTramite(); tramite.setIdabonado(123L); tramite.setUsucrea(7L);
        var tp = new TipoTramite(); tp.setIdtipotramite(tipo); tramite.setIdtipotramite_tipotramite(tp);
        var datos = new Abonados(); datos.setIdabonado(123L);
        var actual = new Abonados(); actual.setIdabonado(123L); actual.setEstado(estado); actual.setNromedidor("MED-123");
        when(abonados.findById(123L)).thenReturn(Optional.of(actual));
        Query query = mock(Query.class); when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(tramites.save(tramite)).thenReturn(tramite);
        request.setTramite(tramite); request.setAbonado(datos); return request;
    }

    @Test void retiroActualizaCuentaYFinalizaTramiteSinSobrescribirMedidor() {
        var request = solicitud(2, 1); servicio.aplicar(request);
        var cuenta = abonados.findById(123L).get();
        assertEquals(3L, cuenta.getEstado()); assertEquals("MED-123", cuenta.getNromedidor());
        assertEquals(3, request.getTramite().getEstado());
        verify(auditoria).saveAuditAtomic(eq("abonados"), eq(123L), any(), eq(7L), isNull(), eq("RETIRO_MEDIDOR"));
        verify(tramites).save(request.getTramite());
    }

    @Test void cambioCategoriaConservaEstadoYMedidor() {
        var request = solicitud(9, 2); var categoria = new Categorias(); categoria.setIdcategoria(4L);
        request.getAbonado().setIdcategoria_categorias(categoria); servicio.aplicar(request);
        var cuenta = abonados.findById(123L).get();
        assertSame(categoria, cuenta.getIdcategoria_categorias()); assertEquals(2L, cuenta.getEstado());
        assertEquals("MED-123", cuenta.getNromedidor());
    }

    @Test void noGuardaTramiteSiNoPuedeGuardarAuditoria() {
        var request = solicitud(2, 1);
        doThrow(new IllegalStateException("auditoria")).when(auditoria).saveAuditAtomic(anyString(), anyLong(), any(), anyLong(), any(), anyString());
        assertThrows(IllegalStateException.class, () -> servicio.aplicar(request));
        verifyNoInteractions(tramites); verify(abonados, never()).save(any());
    }

    @Test void rechazaRetiroDuplicadoYCuentasDiferentes() {
        var request = solicitud(2, 3);
        assertThrows(ResponseStatusException.class, () -> servicio.aplicar(request));
        request.getAbonado().setIdabonado(999L);
        assertThrows(ResponseStatusException.class, () -> servicio.aplicar(request));
        verifyNoInteractions(tramites, auditoria);
    }
}
