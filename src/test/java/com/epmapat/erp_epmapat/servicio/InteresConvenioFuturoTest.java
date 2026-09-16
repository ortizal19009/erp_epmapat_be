package com.epmapat.erp_epmapat.servicio;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.interfaces.FacLite;
import com.epmapat.erp_epmapat.modelo.Tmpinteresxfac;
import com.epmapat.erp_epmapat.repositorio.*;
class InteresConvenioFuturoTest {
 @Test void cuotaFuturaNoReutilizaMoraAntigua() {
  FacturasR facturas=mock(FacturasR.class); TmpinteresxfacR tmp=mock(TmpinteresxfacR.class);
  FacLite cuota=mock(FacLite.class); when(cuota.getId()).thenReturn(1L);
  when(cuota.getFecCrea()).thenReturn(LocalDate.of(2026,12,27));
  when(facturas.getSinCobrarLiteByIds(List.of(1L))).thenReturn(List.of(cuota));
  Tmpinteresxfac anterior=new Tmpinteresxfac(); anterior.setIdfactura(1L); anterior.setInteresapagar(new BigDecimal("38.41"));
  when(tmp.findAllByIdfacturaIn(List.of(1L))).thenReturn(List.of(anterior));
  InteresBatchService servicio=new InteresBatchService(facturas,tmp,mock(InteresesR.class));
  assertEquals(BigDecimal.ZERO,servicio.recalcularInteresesPorFacturas(List.of(1L),LocalDate.of(2026,9,16)).get(1L));
  verify(tmp,never()).saveAll(any());
 }
}
