package com.epmapat.erp_epmapat.config;

import static org.junit.jupiter.api.Assertions.*;
import javax.servlet.DispatcherType;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.filter.CorsFilter;

class CorsConfigTest {
    private static final String ORIGIN = "http://192.168.0.88";

    private MockHttpServletRequest request(String method) {
        MockHttpServletRequest request = new MockHttpServletRequest(method, "/api/th-leave/balances/persona/2");
        request.setServerName("192.168.0.165"); request.setServerPort(8080);
        request.addHeader("Origin", ORIGIN);
        return request;
    }

    @Test void preflightAllowsAuthorizationWithoutRunningApplication() throws Exception {
        var request = request("OPTIONS");
        request.addHeader("Access-Control-Request-Method", "GET");
        request.addHeader("Access-Control-Request-Headers", "authorization");
        var response = new MockHttpServletResponse();
        new CorsConfig().corsFilter().getFilter().doFilter(request, response, (req, res) -> fail("Preflight reached application"));
        assertEquals(200, response.getStatus());
        assertEquals(ORIGIN, response.getHeader("Access-Control-Allow-Origin"));
        assertEquals("authorization", response.getHeader("Access-Control-Allow-Headers"));
    }

    @Test void errorDispatchKeepsCorsOnServerFailure() throws Exception {
        var request = request("GET"); request.setDispatcherType(DispatcherType.ERROR);
        request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
        var response = new MockHttpServletResponse();
        new CorsConfig().corsFilter().getFilter().doFilter(request, response, (req, res) -> response.setStatus(500));
        assertEquals(500, response.getStatus()); assertEquals(ORIGIN, response.getHeader("Access-Control-Allow-Origin"));
    }

    @Test void nestedErrorDispatchRestoresCorsAfterContainerResetsResponse() throws Exception {
        CorsFilter filter = new CorsConfig().corsFilter().getFilter();
        var request = request("GET"); var response = new MockHttpServletResponse();
        filter.doFilter(request, response, (req, res) -> {
            response.reset(); request.setDispatcherType(DispatcherType.ERROR);
            request.setAttribute("javax.servlet.error.request_uri", request.getRequestURI());
            filter.doFilter(request, response, (nestedReq, nestedRes) -> response.setStatus(500));
        });
        assertEquals(500, response.getStatus()); assertEquals(ORIGIN, response.getHeader("Access-Control-Allow-Origin"));
    }

    @Test void authenticationFailureRemainsVisibleToBrowser() throws Exception {
        var response = new MockHttpServletResponse();
        new CorsConfig().corsFilter().getFilter().doFilter(request("GET"), response, (req, res) -> response.setStatus(401));
        assertEquals(401, response.getStatus()); assertEquals(ORIGIN, response.getHeader("Access-Control-Allow-Origin"));
        assertNull(response.getHeader("Access-Control-Allow-Credentials"));
    }
}
