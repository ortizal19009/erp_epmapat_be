package com.epmapat.erp_epmapat.emails.component;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.seguridad.JwtService;
import com.epmapat.erp_epmapat.repositorio.administracion.*;
import com.epmapat.erp_epmapat.modelo.administracion.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
class EmailAdminAccessServiceTest {
    private final JwtService jwt=mock(JwtService.class);private final UsuariosR users=mock(UsuariosR.class);private final VentanasR windows=mock(VentanasR.class);
    private final EmailAdminAccessService access=new EmailAdminAccessService(jwt,users,windows);
    @Test void requestWithoutWebSessionIsRejected(){assertThrows(ResponseStatusException.class,()->access.require(new MockHttpServletRequest(),false));}
    @Test void readPermissionCannotExecuteManualActions(){
        var req=new MockHttpServletRequest();req.addHeader("Authorization","Bearer valid");when(jwt.validateWebToken("valid")).thenReturn(Map.of("sub","2"));
        var user=mock(Usuarios.class);when(user.getEstado()).thenReturn(true);when(users.findById(2L)).thenReturn(Optional.of(user));
        var window=mock(Ventanas.class);when(window.getNombre()).thenReturn("admin-correos");when(window.getPermissions()).thenReturn(1L);when(windows.findByIdusuarioOrderByNombreAsc(2L)).thenReturn(List.of(window));
        assertEquals(2L,access.require(req,false));assertThrows(ResponseStatusException.class,()->access.require(req,true));
    }
    @Test void actorComesFromValidatedSessionAndInactiveUsersCannotOperate(){
        var req=new MockHttpServletRequest();req.addHeader("Authorization","Bearer valid");req.addHeader("X-Operations-Actor","usuario:1");when(jwt.validateWebToken("valid")).thenReturn(Map.of("sub","2"));
        when(users.findById(2L)).thenReturn(Optional.empty());assertThrows(ResponseStatusException.class,()->access.require(req,true));
    }
}
