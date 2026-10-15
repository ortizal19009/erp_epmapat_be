package com.epmapat.erp_epmapat.rrhh;

import static org.mockito.Mockito.*;
import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import org.junit.jupiter.api.*;
import org.springframework.http.*;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.rrhh.controlador.ThEmployeeFileApi;
import com.epmapat.erp_epmapat.rrhh.exception.ThEmployeeFileApiExceptionHandler;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThEmployeeFileApiTest {
    ThEmployeeFileServicio service=mock(ThEmployeeFileServicio.class);
    ThEmployeeFileAccessService access=mock(ThEmployeeFileAccessService.class);
    MockMvc mvc;
    @BeforeEach void setup() {
        when(access.require(any(),anyBoolean())).thenReturn(8L);
        mvc=MockMvcBuilders.standaloneSetup(new ThEmployeeFileApi(service,access)).setControllerAdvice(new ThEmployeeFileApiExceptionHandler()).build();
    }
    @Test void referenceAndUploadUseAuthenticatedActor() throws Exception {
        mvc.perform(post("/api/th-files").contentType(MediaType.APPLICATION_JSON).content("{\"usucrea\":1}" )).andExpect(status().isCreated());
        verify(service).save(any(),eq(8L));
        mvc.perform(multipart("/api/th-files/upload").file(new MockMultipartFile("metadata","","application/json","{}".getBytes()))
            .file(new MockMultipartFile("file","test.pdf","application/pdf","%PDF-1.7".getBytes()))).andExpect(status().isCreated());
        verify(service).upload(any(),any(),eq(8L));
    }
    @Test void deniedWriterCannotRegisterOrArchive() throws Exception {
        when(access.require(any(),eq(true))).thenThrow(new ResponseStatusException(HttpStatus.FORBIDDEN,"Solo lectura"));
        mvc.perform(post("/api/th-files").contentType(MediaType.APPLICATION_JSON).content("{}")).andExpect(status().isForbidden());
        mvc.perform(patch("/api/th-files/3/estado").contentType(MediaType.APPLICATION_JSON).content("{\"estado\":\"ARCHIVADO\"}")).andExpect(status().isForbidden());
        verifyNoInteractions(service);
    }
    @Test void readerChecksAccessAndDoesNotCachePersonalDocuments() throws Exception {
        mvc.perform(get("/api/th-files/persona/2")).andExpect(status().isOk()).andExpect(header().string("Cache-Control","no-store"));
        verify(access).require(any(),eq(false)); verify(service).byPersonal(2L);
    }
    @Test void downloadIsBlockedBeforeReadingStorageWithoutPermission() throws Exception {
        when(access.require(any(),eq(false))).thenThrow(new ResponseStatusException(HttpStatus.UNAUTHORIZED,"Token requerido"));
        mvc.perform(get("/api/th-files/3/download")).andExpect(status().isUnauthorized()); verifyNoInteractions(service);
    }
}
