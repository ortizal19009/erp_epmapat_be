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
    @Mock com.epmapat.erp_epmapat.servicio.MobileReadingTraceValidator validator;
    @Mock com.epmapat.erp_epmapat.servicio.MobileReadingCommitService commits;
    @InjectMocks LecturasApi controller;

    @Test void photoConfirmsExactOperationAndReading() throws Exception {
        org.springframework.test.util.ReflectionTestUtils.setField(controller,"mobileReadingCommitService",commits);
        var reading=new Lecturas();reading.setIdlectura(123L);
        when(service.findById(123L)).thenReturn(Optional.of(reading));
        String op=java.util.UUID.randomUUID().toString();
        when(commits.commitPhoto(eq(op),eq(123L),any(),anyString(),eq(7L),anyString(),anyString()))
            .thenReturn("EPMAPAT-PHOTOS/lecturas/photo.jpg");
        MockMvcBuilders.standaloneSetup(controller).build().perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/lecturas/123/foto")
                .file(new org.springframework.mock.web.MockMultipartFile("file","photo.jpg","image/jpeg",new byte[]{1}))
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .param("usumodi","7").param("operationId",op))
            .andExpect(status().isOk()).andExpect(jsonPath("$.idlectura").value(123))
            .andExpect(jsonPath("$.photoOperationId").value(op))
            .andExpect(jsonPath("$.fotoPath").value("EPMAPAT-PHOTOS/lecturas/photo.jpg"));
    }

    @Test void storageForbiddenReportsStorageSourceInsteadOfAskingForMobileLogin() throws Exception {
        org.springframework.test.util.ReflectionTestUtils.setField(controller,"mobileReadingCommitService",commits);
        var reading=new Lecturas();reading.setIdlectura(123L);
        when(service.findById(123L)).thenReturn(Optional.of(reading));
        when(commits.commitPhoto(anyString(),eq(123L),any(),anyString(),eq(7L),anyString(),anyString()))
            .thenThrow(com.epmapat.erp_epmapat.excepciones.PhotoStorageException.from(
                new com.github.sardine.impl.SardineException("Forbidden",403,"Forbidden")));
        MockMvcBuilders.standaloneSetup(controller)
            .setControllerAdvice(new com.epmapat.erp_epmapat.excepciones.MobilePhotoExceptionHandler()).build().perform(
            org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart("/lecturas/123/foto")
                .file(new org.springframework.mock.web.MockMultipartFile("file","photo.jpg","image/jpeg",new byte[]{1}))
                .accept(org.springframework.http.MediaType.APPLICATION_JSON)
                .param("usumodi","7").param("operationId",java.util.UUID.randomUUID().toString()))
            .andExpect(status().isBadGateway())
            .andExpect(jsonPath("$.errorCode").value("PHOTO_STORAGE_PERMISSION_DENIED"))
            .andExpect(jsonPath("$.upstreamStatus").value(403))
            .andExpect(jsonPath("$.retryable").value(false));
    }

    @Test void uploadConfirmsOnlySavedItemsAndKeepsRejectedItemsPending() throws Exception {
        doAnswer(invocation -> {
            com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto item = invocation.getArgument(0);
            if (Long.valueOf(124).equals(item.getIdlectura())) {
                throw new org.springframework.web.server.ResponseStatusException(
                    org.springframework.http.HttpStatus.BAD_REQUEST, "La sesion GPS debe usar su UUID remoto");
            }
            return null;
        }).when(validator).validate(any(), eq(7L));
        when(service.findById(123L)).thenReturn(Optional.of(new Lecturas()));
        MockMvcBuilders.standaloneSetup(controller).build()
            .perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/lecturas/mobile/upload")
                .param("usumodi", "7").accept(org.springframework.http.MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("[{\"idlectura\":123},{\"idlectura\":124},null,{}]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(1))
            .andExpect(jsonPath("$.error").value(3))
            .andExpect(jsonPath("$.confirmedIds[0]").value(123))
            .andExpect(jsonPath("$.pendingIds[0]").value(124))
            .andExpect(jsonPath("$.allConfirmed").value(false))
            .andExpect(jsonPath("$.results[0].status").value("CONFIRMED"))
            .andExpect(jsonPath("$.results[1].status").value("PENDING"))
            .andExpect(jsonPath("$.results[1].httpStatus").value(400))
            .andExpect(jsonPath("$.results[2].index").value(2));
        verify(service, times(1)).actualizarLecturaConAuditoria(eq(123L), any(), eq(7L), anyString(), anyString());
        verify(service, never()).saveLectura(any());
    }

    @Test void persistenceFailureMustNotConfirmReading() throws Exception {
        when(service.findById(123L)).thenReturn(Optional.of(new Lecturas()));
        when(service.actualizarLecturaConAuditoria(eq(123L), any(), eq(7L), anyString(), anyString()))
            .thenThrow(new RuntimeException("database unavailable"));
        MockMvcBuilders.standaloneSetup(controller).build()
            .perform(org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post("/lecturas/mobile/upload")
                .param("usumodi", "7").accept(org.springframework.http.MediaType.APPLICATION_JSON).contentType(org.springframework.http.MediaType.APPLICATION_JSON)
                .content("[{\"idlectura\":123}]"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ok").value(0))
            .andExpect(jsonPath("$.confirmedIds").isEmpty())
            .andExpect(jsonPath("$.pendingIds[0]").value(123))
            .andExpect(jsonPath("$.results[0].httpStatus").value(500));
    }

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
