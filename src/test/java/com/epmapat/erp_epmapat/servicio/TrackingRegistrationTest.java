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
