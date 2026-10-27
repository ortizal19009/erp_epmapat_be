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
        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean required = requiresWebJwt(path);
        String authorization = request.getHeader("Authorization");
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            if (required) {
                unauthorized(response, "Token WEB requerido");
                return;
            }
        } else {
            try {
                // Identify valid WEB sessions on legacy routes too, for request auditing.
                // Legacy/mobile tokens remain optional outside protected WEB routes.
                Map<String, Object> claims = jwtService.validateWebToken(authorization.substring(7).trim());
                Long userId = Long.valueOf(String.valueOf(claims.get("sub")));
                request.setAttribute("jwtUserId", userId);
                request.setAttribute("jwtPlatform", claims.get("platform"));
            } catch (IllegalArgumentException ex) {
                if (required) {
                    unauthorized(response, "Sesion web invalida o expirada");
                    return;
                }
            }
        }
        filterChain.doFilter(request, response);
    }

    private boolean requiresWebJwt(String uri) {
        return uri.equals("/request-audit") || uri.startsWith("/request-audit/")
                || uri.equals("/api/nc-endosos") || uri.startsWith("/api/nc-endosos/")
                || uri.startsWith("/usrxmodulos")
                || uri.startsWith("/ventanas")
                || uri.startsWith("/access/")
                || uri.startsWith("/usuarios/session")
                || uri.startsWith("/api/backup")
                || uri.equals("/api/th-files") || uri.startsWith("/api/th-files/")
                || uri.equals("/api/th-performance") || uri.startsWith("/api/th-performance/")
                || uri.equals("/api/th-leave") || uri.startsWith("/api/th-leave/");
    }

    private void unauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"message\":\"" + message + "\"}");
    }
}
