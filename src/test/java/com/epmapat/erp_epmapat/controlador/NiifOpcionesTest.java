package com.epmapat.erp_epmapat.controlador;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import com.epmapat.erp_epmapat.controlador.contabilidad.NiifHomologaApi;
import com.epmapat.erp_epmapat.controlador.contabilidad.NiifCuentasApi;
import com.epmapat.erp_epmapat.servicio.contabilidad.NiifHomologaServicio;
import com.epmapat.erp_epmapat.servicio.contabilidad.NiifCuentaServicio;

class NiifOpcionesTest {
   @Test void eliminacionConfirmadaDevuelveTrue() {
      var servicio = mock(NiifHomologaServicio.class); var api = new NiifHomologaApi();
      ReflectionTestUtils.setField(api, "niifhomoServicio", servicio);
      when(servicio.findById(7L)).thenReturn(Optional.empty());
      assertEquals(Boolean.TRUE, api.deleteHomologa(7L).getBody()); verify(servicio).deleteById(7L);
   }

   @Test void listadoSinFiltrosNoProduceErrorPorNombreNulo() {
      var servicio = mock(NiifCuentaServicio.class); var api = new NiifCuentasApi();
      ReflectionTestUtils.setField(api, "niifcueServicio", servicio);
      api.getByCodigoyNombre(null, null); verify(servicio).findByCodigoyNombre("", "");
   }
}
