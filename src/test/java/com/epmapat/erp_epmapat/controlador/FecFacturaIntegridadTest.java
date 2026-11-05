package com.epmapat.erp_epmapat.controlador;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.epmapat.erp_epmapat.config.CorsConfig;
import com.epmapat.erp_epmapat.modelo.Fec_factura;
import com.epmapat.erp_epmapat.servicio.Fec_facturaService;

class FecFacturaIntegridadTest {
    @Test
    void dependenciaAlEliminarDevuelveConflictoConCors() throws Exception {
        var servicio = mock(Fec_facturaService.class);
        var controller = new Fec_facturaApi();
        ReflectionTestUtils.setField(controller, "fecfacServicio", servicio);
        when(servicio.findById(3061759L)).thenReturn(Optional.of(new Fec_factura()));
        doThrow(new DataIntegrityViolationException("evento_outbox_factura_id_fkey"))
                .when(servicio).delete(3061759L);

        MockMvcBuilders.standaloneSetup(controller)
                .addFilters(new CorsConfig().corsFilter().getFilter()).build()
                .perform(delete("/fec_factura/3061759").accept("application/json")
                        .header("Origin", "http://192.168.0.88"))
                .andExpect(status().isConflict())
                .andExpect(header().string("Access-Control-Allow-Origin", "http://192.168.0.88"))
                .andExpect(jsonPath("$.code").value("FEC_FACTURA_INTEGRIDAD"))
                .andExpect(jsonPath("$.message").isNotEmpty());
    }
}
