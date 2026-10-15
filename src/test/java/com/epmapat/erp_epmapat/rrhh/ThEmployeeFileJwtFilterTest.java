package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.seguridad.JwtAuthenticationFilter;
import com.epmapat.erp_epmapat.seguridad.JwtService;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.repositorio.administracion.*;
import com.epmapat.erp_epmapat.rrhh.controlador.ThEmployeeFileApi;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThEmployeeFileJwtFilterTest {
    private final JwtService tokens = new JwtService(new ObjectMapper(), "test-only-secret", 3600);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokens);

    @Test void activeAdministratorCanReadPermissionsWithVerifiedToken() throws Exception {
        var users = mock(UsuariosR.class);
        var user = new Usuarios(); user.setEstado(true);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        var api = new ThEmployeeFileApi(mock(ThEmployeeFileServicio.class),
                new ThEmployeeFileAccessService(users, mock(UsrxmodulosR.class), mock(VentanasR.class)));
        var request = new MockHttpServletRequest("GET", "/erp/api/th-files/permissions");
        request.setContextPath("/erp");
        request.addHeader("Authorization", "Bearer " + tokens.createWebToken(1L, "admin", "ADMIN"));
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> {
            var permissions = api.permissions((javax.servlet.http.HttpServletRequest) req);
            assertEquals(1L, permissions.get("userId"));
            assertEquals(true, permissions.get("canWrite"));
        });
        assertEquals(1L, request.getAttribute("jwtUserId"));
        assertEquals(200, response.getStatus());
    }

    @Test void allFileEndpointsRejectMissingAndInvalidTokens() throws Exception {
        for (String path : new String[]{"/api/th-files", "/api/th-files/permissions", "/api/th-files/upload",
                "/api/th-files/persona/2", "/api/th-files/3/download", "/api/th-files/3/estado"}) {
            for (boolean invalid : new boolean[]{false, true}) {
                var request = new MockHttpServletRequest("GET", path);
                if (invalid) request.addHeader("Authorization", "Bearer invalid");
                var response = new MockHttpServletResponse();
                var chain = mock(FilterChain.class);
                filter.doFilter(request, response, chain);
                assertEquals(401, response.getStatus(), path);
                verifyNoInteractions(chain);
            }
        }
    }

    @Test void preflightAndUnrelatedRoutesRemainUnchanged() throws Exception {
        for (String method : new String[]{"OPTIONS", "GET"}) {
            var request = new MockHttpServletRequest(method,
                    method.equals("OPTIONS") ? "/api/th-files/upload" : "/api/th-files-other");
            var response = new MockHttpServletResponse();
            var chain = mock(FilterChain.class);
            filter.doFilter(request, response, chain);
            verify(chain).doFilter(request, response);
        }
    }
}
