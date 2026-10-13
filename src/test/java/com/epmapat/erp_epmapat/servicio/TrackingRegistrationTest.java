package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.DTO.TrackingSessionDto;
import com.epmapat.erp_epmapat.modelo.TrackingSession;
import com.epmapat.erp_epmapat.repositorio.TrackingSessionR;
import javax.persistence.*;
import java.time.LocalDate;
import java.util.*;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;

class TrackingRegistrationTest {
    @Test void historicalSessionPreservesParentAndAcceptsCaptureDayWithSameScope() {
        var dao=mock(TrackingSessionR.class); var em=mock(EntityManager.class); var query=mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(),any())).thenReturn(query);
        var service=new TrackingServicio();
        ReflectionTestUtils.setField(service,"trackingSessionR",dao);
        ReflectionTestUtils.setField(service,"entityManager",em);
        var source=TrackingSession.builder().id(UUID.randomUUID().toString()).readerId(7L)
            .workDate(LocalDate.of(2026,10,6)).deviceId("device").routeId(8L).build();
        var dto=new TrackingSessionDto(); dto.setSourceSessionId(source.getId()); dto.setReaderId(7L);
        dto.setWorkDate(LocalDate.of(2026,10,12)); dto.setStartTime(dto.getWorkDate().atTime(10,0));
        dto.setDeviceId("device"); dto.setRouteId(8L); dto.setConfirmIdentity(true);
        dto.setId(UUID.nameUUIDFromBytes(("historical-day:"+source.getId()+":"+dto.getWorkDate())
            .getBytes(java.nio.charset.StandardCharsets.UTF_8)).toString());
        when(dao.findById(source.getId())).thenReturn(Optional.of(source));
        when(dao.findById(dto.getId())).thenReturn(Optional.empty());
        when(dao.save(any())).thenAnswer(call -> call.getArgument(0));
        var recovered=service.startSession(dto);
        assertEquals(dto.getWorkDate(),recovered.getWorkDate());
        assertEquals(source.getId(),recovered.getSourceSessionId());
        assertEquals(LocalDate.of(2026,10,6),source.getWorkDate());
        dto.setReaderId(99L);
        assertEquals(409,assertThrows(ResponseStatusException.class,()->service.startSession(dto)).getStatus().value());
        verify(dao,times(1)).save(any());
    }
    @Test void registrationLocksStableKeyRecoversExistingSessionAndDetectsOptInIdentityConflict() {
        var dao=mock(TrackingSessionR.class); var em=mock(EntityManager.class); var query=mock(Query.class);
        when(em.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(),any())).thenReturn(query);
        var service=new TrackingServicio();
        ReflectionTestUtils.setField(service,"trackingSessionR",dao);
        ReflectionTestUtils.setField(service,"entityManager",em);
        var dto=new TrackingSessionDto(); dto.setId(UUID.randomUUID().toString());
        dto.setReaderId(7L); dto.setWorkDate(LocalDate.of(2026,10,13)); dto.setDeviceId("test-device"); dto.setConfirmIdentity(true);
        var existing=TrackingSession.builder().id(dto.getId()).readerId(7L).workDate(dto.getWorkDate()).deviceId(dto.getDeviceId()).build();
        when(dao.findById(dto.getId())).thenReturn(Optional.of(existing));
        assertSame(existing,service.startSession(dto));
        assertSame(existing,service.startSession(dto));
        verify(dao,never()).save(any());
        verify(query,times(2)).setParameter("key","tracking-session:"+dto.getId());
        dto.setWorkDate(LocalDate.of(2026,9,14));
        assertEquals(409,assertThrows(ResponseStatusException.class,()->service.startSession(dto)).getStatus().value());
        dto.setConfirmIdentity(false);
        assertSame(existing,service.startSession(dto));
    }
}
