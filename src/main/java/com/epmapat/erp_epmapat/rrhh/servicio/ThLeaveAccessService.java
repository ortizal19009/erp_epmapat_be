package com.epmapat.erp_epmapat.rrhh.servicio;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.epmapat.erp_epmapat.repositorio.administracion.UsrxmodulosR;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;
import lombok.RequiredArgsConstructor;

/** Uses the existing WEB module/window permissions, not client-supplied actors. */
@Service
@RequiredArgsConstructor
public class ThLeaveAccessService {
    private final UsuariosR usuarios;
    private final UsrxmodulosR modulos;
    private final VentanasR ventanas;

    public Long require(HttpServletRequest request, boolean write) {
        Object value = request.getAttribute("jwtUserId");
        if (value == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token WEB requerido");
        Long userId = Long.valueOf(String.valueOf(value));
        boolean active = usuarios.findById(userId).map(u -> Boolean.TRUE.equals(u.getEstado())).orElse(false);
        if (!active) throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        if (userId.equals(1L)) return userId;
        long minimum = write ? 2L : 1L;
        boolean allowed = modulos.findActiveModuleIdsByUser(userId).contains(5L)
                && ventanas.findByIdusuarioOrderByNombreAsc(userId).stream().anyMatch(v ->
                    v.getNombre() != null && "th-leave".equalsIgnoreCase(v.getNombre().trim().replaceFirst("^/", ""))
                    && v.getPermissions() != null && v.getPermissions() >= minimum);
        if (!allowed) throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "No tiene permiso para " + (write ? "modificar" : "consultar") + " vacaciones, permisos y licencias");
        return userId;
    }
}
