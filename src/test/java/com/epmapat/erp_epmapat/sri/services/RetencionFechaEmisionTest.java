package com.epmapat.erp_epmapat.sri.services;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.sql.Date;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import com.epmapat.erp_epmapat.modelo.contabilidad.*;
import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.repositorio.contabilidad.*;
import com.epmapat.erp_epmapat.repositorio.administracion.DefinirR;

class RetencionFechaEmisionTest {
   private Retenciones caso6962() {
      var r = new Retenciones(); r.setIdrete(181L); r.setSecretencion1("6962");
      r.setNumserie("001001"); r.setEstado(1);
      r.setFechaemision(Date.valueOf("2026-11-04"));
      r.setFechaemiret1(Date.valueOf("2026-11-10"));
      return r;
   }

   @Test void claveUsaFechaRetencionYRechazoExplicitoPermiteRegenerarSinAutorizacion() {
      var r = caso6962(); var repo = mock(RetencionesR.class);
      var fecRepo = mock(Fec_retencionesR.class); var definirRepo = mock(DefinirR.class);
      var f = new Fec_retenciones(); f.setEstado("NO_AUTORIZADO");
      var definir = new Definir(); definir.setRuc("0460028810001");
      when(repo.findById(181L)).thenReturn(Optional.of(r));
      when(repo.save(r)).thenReturn(r);
      when(fecRepo.findById(181L)).thenReturn(Optional.of(f));
      when(definirRepo.findTopByOrderByIddefinirDesc()).thenReturn(definir);
      var service = new RetencionClaveAccesoService(repo, fecRepo, definirRepo, new Modulo11Calculator());
      var clave = service.regenerarClaveAcceso(181L).getClaveacceso();
      assertTrue(clave.startsWith("10112026"));
      assertTrue(service.validarClaveAcceso(clave, r, definir).isValida());
      assertEquals(Date.valueOf("2026-11-04"), r.getFechaemision());
      assertEquals(Date.valueOf("2026-11-10"), RetencionDatosEmision.fecha(r));
   }

   @Test void noPermiteRegenerarAutorizadasNiEstadosHistoricosSinConfirmacion() {
      var r = caso6962(); var f = new Fec_retenciones(); f.setEstado("NO_AUTORIZADO");
      assertThrows(IllegalStateException.class, () -> RetencionDatosEmision.validarRegeneracion(r, null));
      r.setNumautoriza_e("autorizacion existente");
      assertThrows(IllegalStateException.class, () -> RetencionDatosEmision.validarRegeneracion(r, f));
      r.setNumautoriza_e(null); f.setEstado("AUTORIZADA");
      assertThrows(IllegalStateException.class, () -> RetencionDatosEmision.validarRegeneracion(r, f));
   }
}
