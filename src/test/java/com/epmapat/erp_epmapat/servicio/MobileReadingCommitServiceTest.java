package com.epmapat.erp_epmapat.servicio;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.DTO.LecturaUploadItemDto;
import com.epmapat.erp_epmapat.modelo.*;
import com.epmapat.erp_epmapat.repositorio.MobileReadingReceiptR;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.persistence.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

class MobileReadingCommitServiceTest {
    @Test void photoRetryReturnsSamePathWithoutStoringOrAuditingTwice() throws Exception {
        EntityManager em=mock(EntityManager.class); Query query=mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        MobileReadingReceiptR receipts=mock(MobileReadingReceiptR.class);
        Map<String,MobileReadingReceipt> stored=new HashMap<>();
        when(receipts.findById(anyString())).thenAnswer(c -> Optional.ofNullable(stored.get(c.getArgument(0))));
        when(receipts.saveAndFlush(any())).thenAnswer(c -> { MobileReadingReceipt r=c.getArgument(0); stored.put(r.getOperationId(),r); return r; });
        LecturaServicio readings=mock(LecturaServicio.class);
        LecturaFotoStorageService photos=mock(LecturaFotoStorageService.class);
        when(photos.saveIdempotent(anyLong(),any(),anyString(),anyString())).thenReturn("lecturas/photo.jpg");
        MobileReadingCommitService service=new MobileReadingCommitService(em,receipts,readings,new ObjectMapper(),photos);
        String op=UUID.randomUUID().toString();
        var image=new org.springframework.mock.web.MockMultipartFile("file","photo.jpg","image/jpeg",new byte[]{1,2,3});
        assertEquals("lecturas/photo.jpg",service.commitPhoto(op,123L,image,"lecturas",7L,"photo","MODIFICACION"));
        assertEquals("lecturas/photo.jpg",service.commitPhoto(op,123L,image,"lecturas",7L,"photo","MODIFICACION"));
        verify(photos,times(1)).saveIdempotent(anyLong(),any(),anyString(),anyString());
        verify(readings,times(1)).actualizarFotoConAuditoriaAtomica(123L,"lecturas/photo.jpg",7L,"photo","MODIFICACION");
        var changed=new org.springframework.mock.web.MockMultipartFile("file","photo.jpg","image/jpeg",new byte[]{4});
        assertEquals(409,assertThrows(ResponseStatusException.class, () -> service.commitPhoto(op,123L,changed,"lecturas",7L,"photo","MODIFICACION")).getStatus().value());
    }
    @Test void retryAfterCommitReturnsReceiptWithoutRepeatingAuditOrWritingAnOldReading() {
        EntityManager em = mock(EntityManager.class);
        Query query = mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        MobileReadingReceiptR receipts = mock(MobileReadingReceiptR.class);
        LecturaServicio readings = mock(LecturaServicio.class);
        Map<String, MobileReadingReceipt> stored = new HashMap<>();
        when(receipts.findById(anyString())).thenAnswer(call -> Optional.ofNullable(stored.get(call.getArgument(0))));
        when(receipts.saveAndFlush(any())).thenAnswer(call -> {
            MobileReadingReceipt receipt = call.getArgument(0); stored.put(receipt.getOperationId(), receipt); return receipt;
        });
        MobileReadingCommitService service = new MobileReadingCommitService(em, receipts, readings, new ObjectMapper(), mock(LecturaFotoStorageService.class));
        LecturaUploadItemDto dto = new LecturaUploadItemDto(); dto.setIdlectura(123L);
        dto.setOperationId(UUID.randomUUID().toString()); dto.setLecturaactual(10f);
        Lecturas entity = new Lecturas();
        service.commit(dto, entity, 7L, "mobile", "MODIFICACION");
        service.commit(dto, entity, 7L, "mobile", "MODIFICACION");
        verify(readings, times(1)).actualizarLecturaConAuditoriaAtomica(123L, entity, 7L, "mobile", "MODIFICACION");
        verify(receipts, times(1)).saveAndFlush(any());
        dto.setLecturaactual(11f);
        assertEquals(409, assertThrows(ResponseStatusException.class,
            () -> service.commit(dto, entity, 7L, "mobile", "MODIFICACION")).getStatus().value());
    }
}
