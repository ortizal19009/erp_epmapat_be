package com.epmapat.erp_epmapat.servicio.recaudacion;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.transaction.PlatformTransactionManager;
import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.servicio.*;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;

class RecaudacionMontosTest {
    RecaudacionCobroServicio servicio;
    FacturasR facturas = mock(FacturasR.class);
    FacturaServicio facturaServicio = mock(FacturaServicio.class);
    RubroxfacServicio rubros = mock(RubroxfacServicio.class);
    TmpinteresxfacService temporales = mock(TmpinteresxfacService.class);
    InteresBatchService batch = mock(InteresBatchService.class);
    Facturas factura;
    ValorFactDTO dto;
    @BeforeEach void preparar() {
        servicio = new RecaudacionCobroServicio(mock(PlatformTransactionManager.class));
        ReflectionTestUtils.setField(servicio, "facturasR", facturas);
        ReflectionTestUtils.setField(servicio, "facturaServicio", facturaServicio);
        ReflectionTestUtils.setField(servicio, "rubroxfacServicio", rubros);
        ReflectionTestUtils.setField(servicio, "tmpinteresxfacService", temporales);
        ReflectionTestUtils.setField(servicio, "interesBatchService", batch);
        ReflectionTestUtils.setField(servicio, "definirServicio", mock(DefinirServicio.class));
        factura = new Facturas(); factura.setIdfactura(1L);
        Modulos modulo = new Modulos(); modulo.setIdmodulo(27L); modulo.setDescripcion("Convenios");
        factura.setIdmodulo(modulo);
        dto = new ValorFactDTO(); dto.setIdfactura(1L); dto.setSubtotal(110F);
        when(facturas.findAllById(List.of(1L))).thenReturn(List.of(factura));
        when(facturaServicio.findAllById(List.of(1L))).thenReturn(List.of(factura));
        when(rubros.getTotalInteresByFacturas(List.of(1L))).thenReturn(Collections.singletonList(new Object[]{1L,new BigDecimal("10.00")}));
        when(rubros.getSubtotalSinInteresByFacturas(List.of(1L))).thenReturn(Collections.singletonList(new Object[]{1L,new BigDecimal("100.00")}));
    }
    void completar() { ReflectionTestUtils.invokeMethod(servicio,"completarMontosPendientes",List.of(dto)); }
    @Test void convenioConservaCapitalYNoGeneraInteresTemporal() {
        completar(); completar();
        assertEquals(100F,dto.getSubtotal());
        assertEquals(new BigDecimal("110.00"),dto.getTotal());
        assertEquals(27L,dto.getIdmodulo()); assertEquals("Convenios",dto.getModulo());
        verifyNoInteractions(temporales,batch);
    }
    @Test void facturaOrdinariaSumaInteresPersistidoYTemporalUnaVez() {
        factura.getIdmodulo().setIdmodulo(4L);
        when(temporales.findByIdFacturas(List.of(1L))).thenReturn(Map.of(1L,new BigDecimal("2.35")));
        completar(); completar();
        assertEquals(new BigDecimal("112.35"),dto.getTotal());
        assertEquals(new BigDecimal("12.35"),dto.getInteres());
        verifyNoInteractions(batch);
    }
    @Test void respetaExoneracionDeIntereses() {
        factura.setSwinteres(true); completar();
        assertEquals(new BigDecimal("100.00"),dto.getTotal());
        assertEquals(0,dto.getInteres().compareTo(BigDecimal.ZERO));
    }
}
