package com.epmapat.erp_epmapat.rrhh;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.Map;
import javax.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import com.epmapat.erp_epmapat.seguridad.JwtAuthenticationFilter;
import com.epmapat.erp_epmapat.seguridad.JwtService;

class ThLeaveJwtFilterTest {
    JwtService tokens = mock(JwtService.class);
    JwtAuthenticationFilter filter = new JwtAuthenticationFilter(tokens);

    @Test void leaveWithoutTokenIsRejectedUnderWarContext() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/erp/api/th-leave/permissions");
        request.setContextPath("/erp");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(401, response.getStatus());
        verifyNoInteractions(chain);
    }

    @Test void verifiedTokenSetsActor() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/th-leave/requests");
        request.addHeader("Authorization", "Bearer verified");
        when(tokens.validateWebToken("verified")).thenReturn(Map.of("sub", "8", "platform", "WEB"));
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        assertEquals(8L, request.getAttribute("jwtUserId"));
        verify(chain).doFilter(request, response);
    }

    @Test void corsPreflightDoesNotRequireToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/api/th-leave/requests");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);
        filter.doFilter(request, response, chain);
        verify(chain).doFilter(request, response);
    }
}
