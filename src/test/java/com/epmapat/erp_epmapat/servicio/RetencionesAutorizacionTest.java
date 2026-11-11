package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.modelo.contabilidad.Retenciones;
import com.epmapat.erp_epmapat.repositorio.contabilidad.RetencionesR;
import com.epmapat.erp_epmapat.servicio.contabilidad.RetencionesServicio;

class RetencionesAutorizacionTest {
   @Test void persisteAutorizacionSinRegrabarDatosContablesODependencias() {
      var repo = mock(RetencionesR.class);
      var service = new RetencionesServicio(); ReflectionTestUtils.setField(service, "dao", repo);
      var existente = new Retenciones(); existente.setIdrete(181L);
      existente.setNumdoc("documento original"); existente.setSecretencion1("6962");
      var autorizada = new Retenciones(); autorizada.setEstado(1);
      autorizada.setClaveacceso("1".repeat(49)); autorizada.setNumautoriza_e("2".repeat(49));
      autorizada.setFecautoriza("2026-11-10"); autorizada.setAmbiente("2");
      when(repo.findById(181L)).thenReturn(Optional.of(existente));
      when(repo.save(existente)).thenReturn(existente);
      var guardada = service.guardarAutorizacionSri(181L, autorizada);
      verify(repo).save(existente);
      assertEquals("1".repeat(49), guardada.getClaveacceso());
      assertEquals("2".repeat(49), guardada.getNumautoriza_e());
      assertEquals("2".repeat(49), guardada.getNumautoriza());
      assertEquals("2026-11-10", guardada.getFecautoriza());
      assertEquals(1, guardada.getEstado());
      assertEquals("documento original", guardada.getNumdoc());
      assertEquals("6962", guardada.getSecretencion1());
   }

   @Test void sinAutorizacionCompletaNoConsultaNiActualizaLaTabla() {
      var repo = mock(RetencionesR.class);
      var service = new RetencionesServicio(); ReflectionTestUtils.setField(service, "dao", repo);
      var pendiente = new Retenciones(); pendiente.setEstado(0);
      assertThrows(IllegalArgumentException.class, () -> service.guardarAutorizacionSri(181L, pendiente));
      verifyNoInteractions(repo);
   }
}
