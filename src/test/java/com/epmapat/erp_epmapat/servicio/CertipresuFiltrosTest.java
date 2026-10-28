package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.repositorio.contabilidad.*;
import com.epmapat.erp_epmapat.servicio.contabilidad.CertipresuServicio;
import com.epmapat.erp_epmapat.controlador.contabilidad.CertipresuApi;
import com.epmapat.erp_epmapat.modelo.contabilidad.Certipresu;
class CertipresuFiltrosTest {
    CertipresuR repo=mock(CertipresuR.class);
    CertipresuServicio servicio=new CertipresuServicio(repo,mock(PartixcertiR.class));
    @Test void admiteFiltrosIndependientesSinLimitesArtificiales() {
      when(repo.findDesdeHasta(1,null,null,null,null)).thenReturn(List.of(new Certipresu()));
      assertEquals(1,servicio.findDesdeHasta(1,null,null,null,null).size());
      LocalDate fecha=LocalDate.of(2026,1,1);
      servicio.findDesdeHasta(1,20L,null,fecha,null);verify(repo).findDesdeHasta(1,20L,null,fecha,null);
    }
    @Test void rechazaRangosInvertidosONumerosNoPositivos() {
      assertThrows(ResponseStatusException.class,()->servicio.findDesdeHasta(1,20L,10L,null,null));
      assertThrows(ResponseStatusException.class,()->servicio.findDesdeHasta(1,0L,null,null,null));
      assertThrows(ResponseStatusException.class,()->servicio.findDesdeHasta(1,null,null,LocalDate.of(2026,12,31),LocalDate.of(2026,1,1)));
      verifyNoInteractions(repo);
    }
    @Test void endpointAceptaFiltrosOmitidosYVacios() throws Exception {
      when(repo.findDesdeHasta(1,null,null,null,null)).thenReturn(List.of());
      var mvc=MockMvcBuilders.standaloneSetup(new CertipresuApi(servicio)).build();
      mvc.perform(get("/certipresu").accept(org.springframework.http.MediaType.APPLICATION_JSON)).andExpect(status().isOk()).andExpect(content().json("[]"));
      mvc.perform(get("/certipresu").param("tipo","1").param("desdeNum","").param("hastaNum","").param("desdeFecha","").param("hastaFecha","")).andExpect(status().isOk());
      verify(repo,times(2)).findDesdeHasta(1,null,null,null,null);
    }
    @Test void endpointConservaRangosExplicitosYDevuelve400SiSonInvalidos() throws Exception {
      var mvc=MockMvcBuilders.standaloneSetup(new CertipresuApi(servicio)).build();
      mvc.perform(get("/certipresu").param("tipo","2").param("desdeNum","5").param("hastaNum","9").param("desdeFecha","2026-01-01").param("hastaFecha","2026-12-31")).andExpect(status().isOk());
      verify(repo).findDesdeHasta(2,5L,9L,LocalDate.of(2026,1,1),LocalDate.of(2026,12,31));
      mvc.perform(get("/certipresu").param("desdeNum","9").param("hastaNum","5")).andExpect(status().isBadRequest());
    }
}
