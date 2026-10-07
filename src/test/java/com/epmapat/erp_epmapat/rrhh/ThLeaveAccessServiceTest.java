package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.modelo.administracion.Ventanas;
import com.epmapat.erp_epmapat.repositorio.administracion.*;
import com.epmapat.erp_epmapat.rrhh.servicio.ThLeaveAccessService;

class ThLeaveAccessServiceTest {
    UsuariosR users = mock(UsuariosR.class);
    UsrxmodulosR modules = mock(UsrxmodulosR.class);
    VentanasR windows = mock(VentanasR.class);
    ThLeaveAccessService service = new ThLeaveAccessService(users, modules, windows);
    MockHttpServletRequest request = new MockHttpServletRequest();

    void configure(long userId, long permission) {
        request.setAttribute("jwtUserId", userId);
        Usuarios user = new Usuarios(); user.setEstado(true);
        when(users.findById(userId)).thenReturn(Optional.of(user));
        when(modules.findActiveModuleIdsByUser(userId)).thenReturn(List.of(5L));
        Ventanas window = new Ventanas(); window.setNombre("/th-leave"); window.setPermissions(permission);
        when(windows.findByIdusuarioOrderByNombreAsc(userId)).thenReturn(List.of(window));
    }

    @Test void tokenRequired() {
        assertEquals(401, assertThrows(ResponseStatusException.class, () -> service.require(request, true)).getStatus().value());
    }
    @Test void readerCannotWrite() {
        configure(8L, 1L);
        assertEquals(8L, service.require(request, false));
        assertEquals(403, assertThrows(ResponseStatusException.class, () -> service.require(request, true)).getStatus().value());
    }
    @Test void writerNeedsEnabledWebModuleAndWindow() {
        configure(8L, 2L);
        assertEquals(8L, service.require(request, true));
        when(modules.findActiveModuleIdsByUser(8L)).thenReturn(List.of(1L));
        assertThrows(ResponseStatusException.class, () -> service.require(request, true));
    }
    @Test void inactiveAdministratorIsDenied() {
        configure(1L, 2L);
        assertEquals(1L, service.require(request, true));
        Usuarios inactive = new Usuarios(); inactive.setEstado(false);
        when(users.findById(1L)).thenReturn(Optional.of(inactive));
        assertThrows(ResponseStatusException.class, () -> service.require(request, true));
    }
}
