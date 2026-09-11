package com.epmapat.erp_epmapat.seguridad;

import java.io.IOException;
import java.util.Map;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            filterChain.doFilter(request, response);
            return;
        }
        if (!requiresWebJwt(request.getRequestURI())) {
            // Mobile still uses a legacy session token; only WEB routes consume JWTs.
            filterChain.doFilter(request, response);
            return;
        }
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            if (requiresWebJwt(request.getRequestURI())) {
                unauthorized(response, "Token WEB requerido");
                return;
            }
            // Mobile conserva su esquema actual mientras se completa su propia migración.
            filterChain.doFilter(request, response);
            return;
        }
        try {
            Map<String, Object> claims = jwtService.validateWebToken(authorization.substring(7).trim());
            request.setAttribute("jwtUserId", Long.valueOf(String.valueOf(claims.get("sub"))));
            request.setAttribute("jwtPlatform", claims.get("platform"));
            filterChain.doFilter(request, response);
        } catch (IllegalArgumentException ex) {
            unauthorized(response, "Sesión web inválida o expirada");
        }
    }

    private boolean requiresWebJwt(String uri) {
        return uri.startsWith("/usrxmodulos")
                || uri.startsWith("/ventanas")
                || uri.startsWith("/access/")
                || uri.startsWith("/usuarios/session")
                || uri.startsWith("/api/backup");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
