package com.epmapat.erp_epmapat.servicio.contabilidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.modelo.contabilidad.Presupue;
import com.epmapat.erp_epmapat.interfaces.ResumenEjecucionPartida;
import com.epmapat.erp_epmapat.repositorio.contabilidad.*;

class PresupuestoColumnasTest {
   final PresupueR partidas = mock(PresupueR.class);
   final EjecucioR ejecucion = mock(EjecucioR.class);
   final PresupueServicio servicio = new PresupueServicio(partidas, mock(PartixcertiR.class), ejecucion);

   @Test void gastoIncluyeCompromisoDevengadoYPagadoEnUnaConsultaSinModificarRegistros() {
      var p = new Presupue(); p.setIntpre(7L); p.setTotdeven(999.0);
      var sinMovimientos = new Presupue(); sinMovimientos.setIntpre(8L);
      when(partidas.findByTipoCodigoyNombre(2, "", "")).thenReturn(List.of(p, sinMovimientos));
      var resumen = mock(ResumenEjecucionPartida.class);
      when(resumen.getIntpre()).thenReturn(7L);
      when(resumen.getCompromiso()).thenReturn(new BigDecimal("80.50"));
      when(resumen.getDevengado()).thenReturn(new BigDecimal("60.25"));
      when(resumen.getCobpagado()).thenReturn(new BigDecimal("40.00"));
      when(ejecucion.resumenPorPartidas(List.of(7L, 8L))).thenReturn(List.of(resumen));
      servicio.findByTipoCodigoyNombre(2, "", "");
      assertEquals(new BigDecimal("80.50"), p.getEjecCompromiso());
      assertEquals(new BigDecimal("60.25"), p.getEjecDevengado());
      assertEquals(new BigDecimal("40.00"), p.getEjecCobpagado());
      assertEquals(BigDecimal.ZERO, sinMovimientos.getEjecCobpagado());
      assertEquals(999.0, p.getTotdeven());
      verify(ejecucion).resumenPorPartidas(List.of(7L, 8L)); verify(partidas, never()).save(any());
   }

   @Test void ingresosSinMovimientosIncluyenCerosYBusquedaVaciaNoConsultaEjecucion() {
      var p = new Presupue(); p.setIntpre(9L);
      when(partidas.findAllByTippar(1, "11%", "%tasas%")).thenReturn(List.of(p));
      when(ejecucion.resumenPorPartidas(List.of(9L))).thenReturn(List.of());
      assertEquals(BigDecimal.ZERO, servicio.findAllIng("11", "tasas").get(0).getEjecDevengado());
      when(partidas.findByTipoCodigoyNombre(2, "nada", "")).thenReturn(List.of());
      assertTrue(servicio.findByTipoCodigoyNombre(2, "nada", "").isEmpty());
      verify(ejecucion, times(1)).resumenPorPartidas(any());
   }
}
