package com.epmapat.erp_epmapat.servicio.contabilidad;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.contabilidad.Transaci;
import com.epmapat.erp_epmapat.repositorio.contabilidad.TransaciR;

class TransaciConciliacionTest {
   final TransaciR repo = mock(TransaciR.class);
   final AsientoServicio asientos = mock(AsientoServicio.class);
   final EjecucioServicio ejecucion = mock(EjecucioServicio.class);
   final TransaciServicio service = new TransaciServicio(repo, asientos, ejecucion);

   Transaci registro() {
      var t = new Transaci(); t.setInttra(10L); t.setValor(new BigDecimal("125.40"));
      t.setDebcre(1); t.setSwconcili(0); t.setMesconcili(0);
      when(repo.findById(10L)).thenReturn(Optional.of(t)); when(repo.save(t)).thenReturn(t);
      return t;
   }

   @Test void conciliarYDesconciliarPreservaImportesYNoRecalculaAsientos() {
      var original = registro(); var cambio = new Transaci(); cambio.setSwconcili(1); cambio.setMesconcili(11);
      service.updateTransaci(10L, cambio);
      assertEquals(1, original.getSwconcili()); assertEquals(11, original.getMesconcili());
      assertEquals(new BigDecimal("125.40"), original.getValor()); assertEquals(1, original.getDebcre());
      cambio.setSwconcili(0); service.updateTransaci(10L, cambio);
      assertEquals(0, original.getSwconcili()); assertEquals(0, original.getMesconcili());
      verifyNoInteractions(asientos, ejecucion);
   }

   @Test void mesInvalidoSeRechazaAntesDeModificarOGuardar() {
      var original = registro(); var cambio = new Transaci(); cambio.setSwconcili(1); cambio.setMesconcili(0);
      assertThrows(ResponseStatusException.class, () -> service.updateTransaci(10L, cambio));
      assertEquals(0, original.getSwconcili()); verify(repo, never()).save(any());
   }
}
