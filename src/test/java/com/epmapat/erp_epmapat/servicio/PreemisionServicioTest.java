package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.DTO.PreemisionReporteDTO;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.*;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;

class PreemisionServicioTest {
    @Test
    void previewMatchesClosingIncludingPenaltiesAndSurchargesWithoutWrites() {
        FacturasR facturas = mock(FacturasR.class);
        Pliego24R pliegos = mock(Pliego24R.class);
        CategoriaR categorias = mock(CategoriaR.class);
        RubroxfacR rubros = mock(RubroxfacR.class);
        DefinirR definir = mock(DefinirR.class);
        EmisionesR emisiones = mock(EmisionesR.class);
        RecargosxcuentaR recargos = mock(RecargosxcuentaR.class);
        EmisionServicioOptimizadoV2 calculador = new EmisionServicioOptimizadoV2(facturas, pliegos, categorias, rubros, definir, emisiones, recargos);
        Pliego24 pliego = new Pliego24();
        pliego.setAgua(new BigDecimal("0.25"));
        pliego.setSaneamiento(new BigDecimal("0.15"));
        pliego.setPorc(new BigDecimal("0.78"));
        Categorias categoria = new Categorias();
        categoria.setFijoagua(new BigDecimal("3.00"));
        categoria.setFijosanea(new BigDecimal("2.00"));
        when(pliegos._findBloque(anyLong(), anyInt())).thenReturn(pliego);
        when(categorias.getCategoriaById(anyInt())).thenReturn(categoria);
        Facturas factura = new Facturas();
        factura.setIdfactura(30L);
        when(facturas.findById(30L)).thenReturn(Optional.of(factura));
        when(facturas.countPendientesMultaExcluyendoFacturaActual(10L, 30L)).thenReturn(1L);
        when(facturas.findSinCobroAbo(10L)).thenReturn(List.of(99L));
        Definir parametros = new Definir();
        parametros.setRbu(new BigDecimal("470.00"));
        when(definir.findTopByOrderByIddefinirDesc()).thenReturn(parametros);
        Recargosxcuenta recargo = new Recargosxcuenta();
        recargo.setTipo(1);
        when(recargos.findByEmisionAndAbonado(1L, 10L)).thenReturn(List.of(recargo));

        for (int cat : new int[] {1, 2, 4, 9}) {
            for (int consumo : new int[] {0, 34, 35, 70, 71}) {
                for (boolean exencion : new boolean[] {false, true}) {
                    clearInvocations(facturas, rubros);
                    BigDecimal previsto = calculador.previsualizarValores(1L, 10L, 30L, consumo, cat, false, true, exencion, exencion);
                    verify(facturas, never()).save(any());
                    verify(facturas, never()).findById(anyLong());
                    verifyNoInteractions(rubros);
                    BigDecimal cierre = calculador.calcularValores(1L, 10L, 30L, consumo, cat, false, true, exencion, exencion, false);
                    assertEquals(cierre, previsto);
                    verify(facturas).save(factura);
                }
            }
        }
        clearInvocations(facturas, rubros);
        assertNotNull(calculador.previsualizarValores(1L, 10L, null, 20, 1, false, false, false, false));
        verify(facturas, never()).save(any());
        verifyNoInteractions(rubros);
    }

    @Test
    void closedEmissionIsRejectedBeforeReadingOrCalculating() {
        EmisionesR emisiones = mock(EmisionesR.class);
        LecturasR lecturas = mock(LecturasR.class);
        EmisionServicioOptimizadoV2 calculador = mock(EmisionServicioOptimizadoV2.class);
        Emisiones emision = new Emisiones();
        emision.setEstado(1);
        when(emisiones.findById(1L)).thenReturn(Optional.of(emision));
        ResponseStatusException error = assertThrows(ResponseStatusException.class,
                () -> new PreemisionServicio(emisiones, lecturas, calculador).reporte(1L));
        assertEquals(409, error.getRawStatusCode());
        verifyNoInteractions(lecturas, calculador);
    }

    @Test
    void reportGroupsRoutesAndReconcilesTotalsIncludingNegativeConsumption() {
        EmisionesR emisiones = mock(EmisionesR.class);
        LecturasR lecturas = mock(LecturasR.class);
        EmisionServicioOptimizadoV2 calculador = mock(EmisionServicioOptimizadoV2.class);
        Emisiones emision = new Emisiones();
        emision.setEstado(0);
        emision.setEmision("2609");
        when(emisiones.findById(1L)).thenReturn(Optional.of(emision));
        when(lecturas.findByIdemision(1L)).thenReturn(List.of(lectura(1L, 5), lectura(2L, 10), lectura(1L, -5)));
        when(calculador.previsualizarValores(eq(1L), anyLong(), isNull(), anyInt(), eq(1), eq(false), eq(false), eq(false), eq(false)))
                .thenReturn(new BigDecimal("12.25"));
        PreemisionReporteDTO reporte = new PreemisionServicio(emisiones, lecturas, calculador).reporte(1L);
        assertEquals(2, reporte.getRutas().size());
        assertEquals(3, reporte.getCuentas());
        assertEquals(15, reporte.getM3());
        assertEquals(new BigDecimal("36.75"), reporte.getValor());
        assertEquals(1, reporte.getConsumosNegativos());
        assertEquals(2, reporte.getRutas().get(0).getCuentas());
        assertEquals(5, reporte.getRutas().get(0).getM3());
        assertEquals(new BigDecimal("24.50"), reporte.getRutas().get(0).getValor());
        assertFalse(reporte.getRutas().get(0).getDetalle().get(1).getObservacion().isEmpty());
        verify(calculador).previsualizarValores(1L, 1L, null, 0, 1, false, false, false, false);
    }

    private Lecturas lectura(Long idruta, int consumo) {
        Rutas ruta = new Rutas();
        ruta.setIdruta(idruta);
        ruta.setCodigo(idruta.toString());
        ruta.setDescripcion("Ruta " + idruta);
        Rutasxemision relacion = new Rutasxemision();
        relacion.setIdruta_rutas(ruta);
        Categorias categoria = new Categorias();
        categoria.setIdcategoria(1L);
        Abonados abonado = new Abonados();
        abonado.setIdabonado(idruta);
        abonado.setIdcategoria_categorias(categoria);
        Lecturas lectura = new Lecturas();
        lectura.setIdabonado_abonados(abonado);
        lectura.setIdrutaxemision_rutasxemision(relacion);
        lectura.setLecturaanterior(100F);
        lectura.setLecturaactual(100F + consumo);
        return lectura;
    }
}
