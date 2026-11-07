package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.time.ZoneId;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.LiquidafacR;

class FacturacionCuotasVencidasTest {
    FacturacionServicio servicio = new FacturacionServicio();
    LiquidafacR repo = mock(LiquidafacR.class);
    LocalDate hoy = LocalDate.now(ZoneId.of("America/Guayaquil"));
    @BeforeEach void setup() { ReflectionTestUtils.setField(servicio,"liquidafacR",repo); }
    Liquidafac cuota(long id, LocalDate fecha, int pagado) {
        Facturacion cabecera = new Facturacion(); cabecera.setIdfacturacion(id);
        Facturas factura = new Facturas(); factura.setFeccrea(fecha); factura.setPagado(pagado); factura.setEstado(1L);
        Liquidafac liq = new Liquidafac(); liq.setIdfacturacion_facturacion(cabecera); liq.setIdfactura_facturas(factura); liq.setValor(25F);
        return liq;
    }
    @Test void separaVencidasDeHoyYFuturasSinContarPagadas() {
        when(repo.findPendientesFacturacionConDetalle()).thenReturn(List.of(cuota(1,hoy.minusDays(3),0),cuota(1,hoy,0),cuota(1,hoy.plusDays(30),0),cuota(1,hoy.minusDays(10),1)));
        var dto=servicio.findFacturacionesConCuotasPendientes(null,null,null,null,null).get(0);
        assertEquals(3L,dto.getPlanillasPendientes()); assertEquals(1L,dto.getPlanillasPagadas());
        assertEquals(1L,dto.getCuotasVencidas()); assertEquals(0,new BigDecimal("25").compareTo(dto.getValorVencido()));
        assertEquals(0,new BigDecimal("75").compareTo(dto.getValorPendiente())); assertEquals(hoy.minusDays(3),dto.getPrimeraCuotaVencida());
    }
    @Test void futurasSinMoraYFechasAusentesNoSeAsumenVencidas() {
        when(repo.findPendientesFacturacionConDetalle()).thenReturn(List.of(cuota(1,hoy.plusDays(1),0),cuota(1,null,0)));
        var dto=servicio.findFacturacionesConCuotasPendientes(null,null,null,null,null).get(0);
        assertEquals(0L,dto.getCuotasVencidas()); assertEquals(BigDecimal.ZERO,dto.getValorVencido()); assertNull(dto.getPrimeraCuotaVencida());
    }
    @Test void excluyeAnuladasEliminadasYLiquidacionesSinPlanilla() {
        var anulada=cuota(1,hoy.minusDays(2),0); anulada.getIdfactura_facturas().setFechaanulacion(hoy);
        var eliminada=cuota(1,hoy.minusDays(2),0); eliminada.getIdfactura_facturas().setFechaeliminacion(hoy);
        var liqEliminada=cuota(1,hoy.minusDays(2),0); liqEliminada.setFechaeliminacion(java.sql.Date.valueOf(hoy));
        var sinPlanilla=cuota(1,hoy.minusDays(2),0); sinPlanilla.setIdfactura_facturas(null);
        when(repo.findPendientesFacturacionConDetalle()).thenReturn(List.of(anulada,eliminada,liqEliminada,sinPlanilla));
        assertTrue(servicio.findFacturacionesConCuotasPendientes(null,null,null,null,null).isEmpty());
    }
    @Test void calculaPorFacturacionYConservaLaFechaVencidaMasAntigua() {
        when(repo.findPendientesFacturacionConDetalle()).thenReturn(List.of(cuota(1,hoy.minusDays(1),0),cuota(2,hoy.plusDays(1),0),cuota(1,hoy.minusDays(30),0)));
        var lista=servicio.findFacturacionesConCuotasPendientes(null,null,null,null,null);
        assertEquals(2L,lista.get(0).getCuotasVencidas()); assertEquals(hoy.minusDays(30),lista.get(0).getPrimeraCuotaVencida());
        assertEquals(0L,lista.get(1).getCuotasVencidas());
    }
    @Test void unaCuotaAntiguaPagadaNoMantieneMora() {
        when(repo.findPendientesFacturacionConDetalle()).thenReturn(List.of(cuota(1,hoy.minusDays(5),1),cuota(1,hoy.plusDays(5),0)));
        var dto=servicio.findFacturacionesConCuotasPendientes(null,null,null,null,null).get(0);
        assertEquals(0L,dto.getCuotasVencidas()); assertEquals(1L,dto.getPlanillasPendientes());
    }
}
