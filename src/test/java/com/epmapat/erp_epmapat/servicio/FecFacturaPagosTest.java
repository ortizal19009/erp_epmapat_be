package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
class FecFacturaPagosTest {
    Fec_facturaService servicio = new Fec_facturaService();
    FacturasR facturas = mock(FacturasR.class);
    Fec_factura_detallesR detalles = mock(Fec_factura_detallesR.class);
    Fec_factura_detalles_impuestosR impuestos = mock(Fec_factura_detalles_impuestosR.class);
    Fec_factura_pagosR pagos = mock(Fec_factura_pagosR.class);
    @BeforeEach void setup() {
        ReflectionTestUtils.setField(servicio,"facturasR",facturas);
        ReflectionTestUtils.setField(servicio,"fecFacturaDetallesR",detalles);
        ReflectionTestUtils.setField(servicio,"fecFacturaDetallesImpuestosR",impuestos);
        ReflectionTestUtils.setField(servicio,"fecFacturaPagosR",pagos);
        ReflectionTestUtils.setField(servicio,"definirR",mock(DefinirR.class));
        Facturas factura = new Facturas(); factura.setIdfactura(1L); factura.setFormapago(1L);
        when(facturas.findById(1L)).thenReturn(Optional.of(factura));
        when(detalles.findByIdfactura(1L)).thenReturn(List.of(detalle(1L,"100"),detalle(5L,"5"),detalle(165L,"2")));
        when(impuestos.findByIdfacturadetalleIn(anyList())).thenReturn(List.of(impuesto("100","4"),impuesto("5","0"),impuesto("2","0")));
    }
    Fec_factura_detalles detalle(long id, String valor) {
        Fec_factura_detalles d = new Fec_factura_detalles(); d.setIdfacturadetalle(id);
        d.setCantidad(BigDecimal.ONE); d.setPreciounitario(new BigDecimal(valor)); d.setDescuento(BigDecimal.ZERO); return d;
    }
    Fec_factura_detalles_impuestos impuesto(String base, String codigo) {
        Fec_factura_detalles_impuestos i = new Fec_factura_detalles_impuestos();
        i.setCodigoimpuesto("2"); i.setCodigoporcentaje(codigo); i.setBaseimponible(new BigDecimal(base)); return i;
    }
    @Test void pagoIncluyeInteresesRubro165EIva() {
        servicio.generarFecFacturaPagos(1L,0L);
        ArgumentCaptor<Fec_factura_pagos> captor = ArgumentCaptor.forClass(Fec_factura_pagos.class);
        verify(pagos).save(captor.capture());
        assertEquals(new BigDecimal("122.00"),captor.getValue().getTotal());
        assertEquals("01",captor.getValue().getFormapago());
        verify(facturas,never()).getForIntereses(anyLong());
    }
    @Test void diagnosticoExponeDiferenciaSinModificarPagos() {
        Fec_factura_pagos pago = new Fec_factura_pagos(); pago.setTotal(new BigDecimal("100"));
        when(pagos.getByIdfactura(1L)).thenReturn(List.of(pago));
        Map<String,Object> resultado = servicio.construirValidacionSri(1L);
        assertEquals(new BigDecimal("22.00"),resultado.get("diferenciaPagos"));
        assertEquals(false,resultado.get("pagosCoinciden")); verify(pagos,never()).save(any());
    }
    @Test void respetaDescuentoYRedondeaIvaPorLineaComoXml() {
        Fec_factura_detalles d = detalle(1L,"1.00"); d.setDescuento(new BigDecimal("0.01"));
        when(detalles.findByIdfactura(1L)).thenReturn(List.of(d));
        when(impuestos.findByIdfacturadetalleIn(anyList())).thenReturn(List.of(impuesto("0.99","4")));
        assertEquals(new BigDecimal("1.14"),servicio.construirValidacionSri(1L).get("importeTotal"));
    }
    Fec_factura pendiente(String estado) {
        Fec_facturaR repo = mock(Fec_facturaR.class);
        ReflectionTestUtils.setField(servicio,"dao",repo);
        ReflectionTestUtils.setField(servicio,"entityManager",mock(javax.persistence.EntityManager.class));
        Fec_factura f = new Fec_factura(); f.setIdfactura(1L); f.setEstado(estado); f.setClaveacceso("clave-original");
        when(repo.findById(1L)).thenReturn(Optional.of(f)); return f;
    }
    @Test void sincronizaSoloImporteSinCambiarClaveEstadoNiFormaPago() {
        Fec_factura f = pendiente("I");
        Fec_factura_pagos pago = new Fec_factura_pagos(); pago.setTotal(new BigDecimal("100")); pago.setFormapago("20");
        when(pagos.getByIdfactura(1L)).thenReturn(List.of(pago));
        servicio.sincronizarPagoPendiente(1L);
        assertEquals(new BigDecimal("122.00"),pago.getTotal()); assertEquals("20",pago.getFormapago());
        assertEquals("I",f.getEstado()); assertEquals("clave-original",f.getClaveacceso());
    }
    @Test void noTocaAutorizadasNiIntentosPrevios() {
        pendiente("A"); assertThrows(IllegalArgumentException.class,()->servicio.sincronizarPagoPendiente(1L));
        Fec_factura f = pendiente("I"); f.setIntentosAutorizacion(1);
        assertThrows(IllegalArgumentException.class,()->servicio.sincronizarPagoPendiente(1L));
        verify(pagos,never()).save(any());
    }
    @Test void noRedistribuyePagosMultiples() {
        pendiente("I");
        when(pagos.getByIdfactura(1L)).thenReturn(List.of(new Fec_factura_pagos(),new Fec_factura_pagos()));
        assertThrows(IllegalArgumentException.class,()->servicio.sincronizarPagoPendiente(1L));
        verify(pagos,never()).save(any());
    }

}
