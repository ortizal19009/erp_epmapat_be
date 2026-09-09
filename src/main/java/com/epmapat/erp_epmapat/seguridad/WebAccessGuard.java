package com.epmapat.erp_epmapat.seguridad;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

@Component
public class WebAccessGuard {
    private static final Long ADMIN_USER_ID = 1L;

    public void requireSelfOrAdmin(HttpServletRequest request, Long targetUserId) {
        Long authenticatedUserId = authenticatedUserId(request);
        if (!ADMIN_USER_ID.equals(authenticatedUserId) && !authenticatedUserId.equals(targetUserId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No tiene acceso al perfil de otro usuario");
        }
    }

    public void requireAdmin(HttpServletRequest request) {
        if (!ADMIN_USER_ID.equals(authenticatedUserId(request))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Se requieren permisos de administración");
        }
    }

    private Long authenticatedUserId(HttpServletRequest request) {
        Object value = request.getAttribute("jwtUserId");
        if (value == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token WEB requerido");
        return Long.valueOf(String.valueOf(value));
    }
}
