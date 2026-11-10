package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import javax.persistence.EntityManager;
import javax.persistence.Query;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.repositorio.*;

class FacturaDependenciasSriTest {
    final FacturaServicio servicio = new FacturaServicio();
    final EntityManager em = mock(EntityManager.class);
    final AuditoriaGenericaService auditoria = mock(AuditoriaGenericaService.class);
    final Fec_facturaR cabecera = mock(Fec_facturaR.class);
    final Query consulta = mock(Query.class);

    @BeforeEach void preparar() {
        ReflectionTestUtils.setField(servicio, "entityManager", em);
        ReflectionTestUtils.setField(servicio, "auditoriaGenericaService", auditoria);
        ReflectionTestUtils.setField(servicio, "fecFacturaR", cabecera);
        ReflectionTestUtils.setField(servicio, "fecFacturaDetallesR", mock(Fec_factura_detallesR.class));
        ReflectionTestUtils.setField(servicio, "fecFacturaDetallesImpuestosR", mock(Fec_factura_detalles_impuestosR.class));
        ReflectionTestUtils.setField(servicio, "fecFacturaPagosR", mock(Fec_factura_pagosR.class));
        ReflectionTestUtils.setField(servicio, "fecFacturaLogR", mock(Fec_factura_logR.class));
        when(cabecera.existsById(3084458L)).thenReturn(true);
        when(em.createNativeQuery(anyString())).thenReturn(consulta);
        when(consulta.setParameter(anyString(), any())).thenReturn(consulta);
        when(consulta.getSingleResult()).thenReturn("[{\"factura_id\":3084458}]");
    }

    @Test void respaldaYEliminaLasTresDependenciasAntesDeBorrarLaCabecera() {
        servicio.eliminarFacturaElectronicaEnCascada(3084458L, 7L, "Correccion");
        var orden = inOrder(auditoria, em, consulta, cabecera);
        for (String tabla : java.util.List.of("evento_outbox", "comprobante_documento", "sri_autorizacion_control")) {
            orden.verify(auditoria).saveAuditAtomic(eq(tabla), eq(3084458L), any(), eq(7L), eq("Correccion"), eq("ELIMINACION"));
            orden.verify(em).createNativeQuery("DELETE FROM public." + tabla + " WHERE factura_id = :id");
            orden.verify(consulta).executeUpdate();
        }
        orden.verify(cabecera).deleteByIdfactura(3084458L);
    }

    @Test void unFalloDeRespaldoImpideBorrarLosDocumentos() {
        doThrow(new IllegalStateException("Sin respaldo")).when(auditoria).saveAuditAtomic(anyString(), anyLong(), any(), anyLong(), anyString(), anyString());
        assertThrows(IllegalStateException.class, () -> servicio.eliminarFacturaElectronicaEnCascada(3084458L, 7L, "Correccion"));
        verify(consulta, never()).executeUpdate(); verify(cabecera, never()).deleteByIdfactura(anyLong());
    }

    @Test void admiteInstalacionesSinLasTablasDePosautorizacion() {
        when(consulta.getSingleResult()).thenReturn(null);
        servicio.eliminarFacturaElectronicaEnCascada(3084458L, 7L, "Correccion");
        verifyNoInteractions(auditoria); verify(consulta, never()).executeUpdate();
        verify(cabecera).deleteByIdfactura(3084458L);
    }
}
