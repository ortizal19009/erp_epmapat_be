package com.epmapat.erp_epmapat.servicio;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import com.epmapat.erp_epmapat.modelo.Emisiones;
import com.epmapat.erp_epmapat.repositorio.EmisionesR;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.server.ResponseStatusException;
class EmisionNuevaEstadoTest {
    @Test void soloPermiteCrearCuandoLaUltimaEstaCerrada() {
        EmisionesR repo = mock(EmisionesR.class);
        EmisionServicio servicio = new EmisionServicio();
        ReflectionTestUtils.setField(servicio, "dao", repo);
        Emisiones ultima = new Emisiones();
        when(repo.findFirstByOrderByEmisionDesc()).thenReturn(ultima);
        Emisiones nueva = new Emisiones();
        for (int estado : new int[]{0, 2, 6}) {
            ultima.setEstado(estado);
            assertThrows(ResponseStatusException.class, () -> servicio.save(nueva));
        }
        verify(repo, never()).save(any());
        ultima.setEstado(1);
        servicio.save(nueva);
        verify(repo).save(nueva);
    }
    @Test void bloqueaSiExisteOtraEmisionAbiertaAunqueLaUltimaEsteCerrada() {
        EmisionesR repo = mock(EmisionesR.class);
        EmisionServicio servicio = new EmisionServicio();
        ReflectionTestUtils.setField(servicio, "dao", repo);
        Emisiones ultima = new Emisiones(); ultima.setEstado(1);
        when(repo.findFirstByOrderByEmisionDesc()).thenReturn(ultima);
        when(repo.findFirstByEstadoOrderByEmisionDesc(0)).thenReturn(new Emisiones());
        assertThrows(ResponseStatusException.class, () -> servicio.save(new Emisiones()));
        verify(repo, never()).save(any());
    }
}
