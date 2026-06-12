package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import com.epmapat.erp_epmapat.modelo.Facturas;

class FacturaServicioUpdateTest {

    @Test
    void mergeFactura_shouldPreserveExistingNonNullValuesWhenIncomingFieldsAreNull() {
        Facturas existing = new Facturas();
        existing.setIdfactura(10L);
        existing.setPorcexoneracion(10L);
        existing.setRazonexonera("motivo anterior");
        existing.setTotaltarifa(new BigDecimal("12.50"));
        existing.setEstado(1L);
        existing.setFechaanulacion(LocalDate.of(2026, 6, 12));

        Facturas incoming = new Facturas();
        incoming.setPorcexoneracion(null);
        incoming.setRazonexonera(null);
        incoming.setTotaltarifa(null);
        incoming.setEstado(null);
        incoming.setFechaanulacion(null);

        FacturaServicio.mergeFactura(existing, incoming);

        assertNotNull(existing);
        assertEquals(10L, existing.getPorcexoneracion());
        assertEquals("motivo anterior", existing.getRazonexonera());
        assertEquals(new BigDecimal("12.50"), existing.getTotaltarifa());
        assertEquals(1L, existing.getEstado());
        assertEquals(LocalDate.of(2026, 6, 12), existing.getFechaanulacion());
    }
}
