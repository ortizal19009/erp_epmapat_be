package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.Facturas;
import com.epmapat.erp_epmapat.repositorio.FacturasR;
import com.epmapat.erp_epmapat.controlador.FacturasApi;
class FacturaMotivoLongitudTest {
    FacturaServicio servicio=new FacturaServicio();
    FacturasR repo=mock(FacturasR.class);
    AuditoriaGenericaService auditoria=mock(AuditoriaGenericaService.class);
    FacturaMotivoLongitudTest(){ReflectionTestUtils.setField(servicio,"dao",repo);ReflectionTestUtils.setField(servicio,"auditoriaGenericaService",auditoria);}
    @Test void limite254EsAceptadoYNoSeRecorta() {
        assertDoesNotThrow(()->FacturaServicio.validarMotivoFactura("a".repeat(254)));
        assertDoesNotThrow(()->FacturaServicio.validarMotivoFactura(null));
        var spy=spy(servicio);var factura=new Facturas();factura.setIdfactura(1L);
        doReturn(Optional.of(factura)).when(spy).findById(1L);doReturn(factura).when(spy).save(factura);
        String motivo="á".repeat(254);spy.ejecutarAnulacion(1L,motivo,7L);assertEquals(motivo,factura.getRazonanulacion());
    }
    @Test void anularCon255CaracteresSeRechazaAntesDeAuditarOModificar() {
        var error=assertThrows(ResponseStatusException.class,()->servicio.ejecutarAnulacion(1L,"a".repeat(255),7L));
        assertEquals(400,error.getRawStatusCode());verifyNoInteractions(repo,auditoria);
    }
    @Test void bajaConMotivoLargoSeRechazaAntesDeGuardar() {
        assertThrows(ResponseStatusException.class,()->servicio.ejecutarEliminacionLogica(1L,"a".repeat(255),7L));verifyNoInteractions(repo,auditoria);
    }
    @Test void guardadoGenericoTambienProtegeLosCamposDeMotivo() {
        var factura=new Facturas();factura.setRazonanulacion("a".repeat(255));
        assertThrows(ResponseStatusException.class,()->servicio.save(factura));
        factura.setRazonanulacion(null);factura.setRazoneliminacion("a".repeat(255));
        assertThrows(ResponseStatusException.class,()->servicio.save(factura));verifyNoInteractions(repo,auditoria);
    }
    @Test void endpointDevuelve400ConMensajeClaroSinEjecutarLaOperacion() throws Exception {
        var api=new FacturasApi();ReflectionTestUtils.setField(api,"facServicio",servicio);
        var mvc=MockMvcBuilders.standaloneSetup(api).build();
        String json="{\"idfactura\":1,\"accion\":\"ANULACION\",\"idusuario\":7,\"motivo\":\""+"a".repeat(255)+"\"}";
        mvc.perform(post("/facturas/anulaciones-bajas").contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON).content(json))
          .andExpect(status().isBadRequest()).andExpect(jsonPath("$.message").value(org.hamcrest.Matchers.containsString("254")));
        verifyNoInteractions(repo,auditoria);
    }
}
