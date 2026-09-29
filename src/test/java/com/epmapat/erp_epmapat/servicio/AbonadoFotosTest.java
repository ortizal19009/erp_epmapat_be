package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.nio.file.Path;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.epmapat.erp_epmapat.controlador.AbonadosApi;
import com.epmapat.erp_epmapat.modelo.Abonados;
import com.epmapat.erp_epmapat.repositorio.AbonadosR;
import com.epmapat.erp_epmapat.servicio.administracion.LocalStorageService;

class AbonadoFotosTest {
    @TempDir Path storageDirectory;
    private AbonadoServicio service;
    private Abonados abonado;
    private MockMvc mvc;

    @BeforeEach void setup() {
        abonado = new Abonados();
        abonado.setIdabonado(123L);
        abonado.setNromedidor("MED-123");
        abonado.setFotocasaPath("ABONADOS/CASAS/anterior.png");
        abonado.setFotomedidorPath("ABONADOS/MEDIDORES/anterior.png");
        AbonadosR repository = mock(AbonadosR.class);
        when(repository.findById(123L)).thenAnswer(inv -> Optional.of(abonado));
        when(repository.save(any(Abonados.class))).thenAnswer(inv -> inv.getArgument(0));
        service = new AbonadoServicio();
        ReflectionTestUtils.setField(service, "dao", repository);
        ReflectionTestUtils.setField(service, "auditoriaService", mock(AuditoriaGenericaService.class));
        LocalStorageService storage = new LocalStorageService(mock(RuntimeEnvironmentService.class));
        ReflectionTestUtils.setField(storage, "basePath", storageDirectory.toString());
        AbonadosApi api = new AbonadosApi();
        ReflectionTestUtils.setField(api, "aboServicio", service);
        ReflectionTestUtils.setField(api, "abonadoFotoStorageService", new AbonadoFotoStorageService(storage));
        mvc = MockMvcBuilders.standaloneSetup(api).build();
    }

    @Test void editarSinFotosConservaAmbasReferencias() {
        Abonados cambios = new Abonados();
        cambios.setNromedidor("MED-123");
        service.actualizarAbonadoConAuditoria(123L, cambios, 1L, "Editar", "MODIFICACION");
        assertEquals("ABONADOS/CASAS/anterior.png", abonado.getFotocasaPath());
        assertEquals("ABONADOS/MEDIDORES/anterior.png", abonado.getFotomedidorPath());
    }

    @Test void editarConRutasVaciasConservaAmbasReferencias() {
        Abonados cambios = new Abonados();
        cambios.setNromedidor("MED-123");
        cambios.setFotocasaPath("");
        cambios.setFotomedidorPath(" ");
        service.actualizarAbonadoConAuditoria(123L, cambios, 1L, "Editar", "MODIFICACION");
        assertEquals("ABONADOS/CASAS/anterior.png", abonado.getFotocasaPath());
        assertEquals("ABONADOS/MEDIDORES/anterior.png", abonado.getFotomedidorPath());
    }

    @Test void subirAmbasFotosPermiteRecuperarLosMismosBytes() throws Exception {
        byte[] casa = java.util.Base64.getDecoder().decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAQAAAC1HAwCAAAAC0lEQVR42mP8/x8AAwMCAO+a0foAAAAASUVORK5CYII=");
        byte[] medidor = casa.clone();
        mvc.perform(multipart("/abonados/123/fotos")
                .file(new MockMultipartFile("fotocasa", "casa.png", "image/png", casa))
                .file(new MockMultipartFile("fotomedidor", "medidor.png", "image/png", medidor))
                .param("usumodi", "1").accept("application/json"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.fotocasaPath").value(abonado.getFotocasaPath()));
        assertTrue(abonado.getFotocasaPath().startsWith("ABONADOS/CASAS/"));
        assertTrue(abonado.getFotomedidorPath().startsWith("ABONADOS/MEDIDORES/"));
        mvc.perform(get("/abonados/123/fotocasa")).andExpect(status().isOk())
            .andExpect(content().contentType("image/png")).andExpect(content().bytes(casa))
            .andExpect(header().string("Cache-Control", "no-store"));
        mvc.perform(get("/abonados/123/fotomedidor")).andExpect(status().isOk())
            .andExpect(content().bytes(medidor));
    }

    @Test void reemplazarSoloCasaNoBorraFotoMedidor() throws Exception {
        mvc.perform(multipart("/abonados/123/fotos")
                .file(new MockMultipartFile("fotocasa", "casa.jpg", "image/jpeg", new byte[] {1, 2, 3})))
            .andExpect(status().isOk());
        assertNotEquals("ABONADOS/CASAS/anterior.png", abonado.getFotocasaPath());
        assertEquals("ABONADOS/MEDIDORES/anterior.png", abonado.getFotomedidorPath());
    }

    @Test void cargaVaciaNoModificaReferencias() throws Exception {
        mvc.perform(multipart("/abonados/123/fotos")).andExpect(status().isBadRequest());
        assertEquals("ABONADOS/CASAS/anterior.png", abonado.getFotocasaPath());
    }
}
