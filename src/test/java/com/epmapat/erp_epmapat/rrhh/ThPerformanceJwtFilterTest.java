package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Optional;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.epmapat.erp_epmapat.seguridad.*;
import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;
import com.epmapat.erp_epmapat.repositorio.administracion.*;
import com.epmapat.erp_epmapat.rrhh.controlador.ThPerformanceApi;
import com.epmapat.erp_epmapat.rrhh.servicio.*;

class ThPerformanceJwtFilterTest {
    private final JwtService tokens = new JwtService(new ObjectMapper(), "test-only-secret", 3600);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokens);

    @Test void administratorCanReadSelectedEmployeesReviewsUnderWarContext() throws Exception {
        var users = mock(UsuariosR.class);
        var user = new Usuarios(); user.setEstado(true);
        when(users.findById(1L)).thenReturn(Optional.of(user));
        var service = mock(ThPerformanceServicio.class);
        var api = new ThPerformanceApi(new ThPerformanceAccessService(users,
                mock(UsrxmodulosR.class), mock(VentanasR.class)), service);
        var request = new MockHttpServletRequest("GET", "/erp/api/th-performance/persona/6");
        request.setContextPath("/erp");
        request.addHeader("Authorization", "Bearer " + tokens.createWebToken(1L, "admin", "ADMIN"));
        var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) ->
                api.listar(6L, 0, 5, (javax.servlet.http.HttpServletRequest) req));
        assertEquals(200, response.getStatus());
        verify(service).listar(6L, 0, 5);
    }

    @Test void performanceRoutesRejectMissingAndInvalidTokens() throws Exception {
        for (String path : new String[]{"/api/th-performance", "/api/th-performance/permissions",
                "/api/th-performance/persona/6"}) {
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
}
