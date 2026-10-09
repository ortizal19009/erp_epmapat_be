package com.epmapat.erp_epmapat.controlador;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import com.epmapat.erp_epmapat.modelo.Lecturas;
import com.epmapat.erp_epmapat.modelo.Novedad;
import com.epmapat.erp_epmapat.servicio.LecturaServicio;

@ExtendWith(MockitoExtension.class)
class LecturasMobileContractTest {
    @Mock LecturaServicio service;
    @InjectMocks LecturasApi controller;

    @Test void mobileReadbackUsesFlatIdsAndIncludesTrace() throws Exception {
        Lecturas reading = new Lecturas();
        reading.setIdlectura(123L);
        Novedad novelty = new Novedad();
        novelty.setIdnovedad(9L);
        reading.setIdnovedad_novedades(novelty);
        reading.setTrackingSessionId("101ea23a-4977-48b9-b35b-41c1c8d4fd72");
        reading.setReadingLatitude(-0.25);
        reading.setReadingLongitude(-78.5);
        reading.setReadingAccuracy(6.0);
        when(service.findById(123L)).thenReturn(Optional.of(reading));
        MockMvcBuilders.standaloneSetup(controller).build()
            .perform(get("/lecturas/123/mobile"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.idnovedad").value(9))
            .andExpect(jsonPath("$.trackingSessionId").value(reading.getTrackingSessionId()))
            .andExpect(jsonPath("$.readingLatitude").value(-0.25))
            .andExpect(jsonPath("$.readingLongitude").value(-78.5))
            .andExpect(jsonPath("$.readingAccuracy").value(6.0));
    }
}
