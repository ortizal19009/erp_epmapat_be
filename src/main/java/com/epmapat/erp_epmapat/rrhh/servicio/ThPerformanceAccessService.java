package com.epmapat.erp_epmapat.rrhh.servicio;

import javax.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import com.epmapat.erp_epmapat.repositorio.administracion.UsuariosR;
import com.epmapat.erp_epmapat.repositorio.administracion.UsrxmodulosR;
import com.epmapat.erp_epmapat.repositorio.administracion.VentanasR;
import lombok.RequiredArgsConstructor;

@Service @RequiredArgsConstructor
public class ThPerformanceAccessService {
    private final UsuariosR usuarios;
    private final UsrxmodulosR modulos;
    private final VentanasR ventanas;

    public Long require(HttpServletRequest request, boolean write) {
        Object value = request.getAttribute("jwtUserId");
        if (value == null) throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token WEB requerido");
        Long id = Long.valueOf(String.valueOf(value));
        if (!usuarios.findById(id).map(u -> Boolean.TRUE.equals(u.getEstado())).orElse(false))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Usuario inactivo");
        if (id == 1L) return id;
        long minimum = write ? 2 : 1;
        if (!modulos.findActiveModuleIdsByUser(id).contains(5L)
                || ventanas.findByIdusuarioOrderByNombreAsc(id).stream().noneMatch(v ->
                    v.getNombre() != null && "th-performance".equalsIgnoreCase(v.getNombre().trim().replaceFirst("^/", ""))
                    && v.getPermissions() != null && v.getPermissions() >= minimum))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Sin permiso para " + (write ? "registrar" : "consultar") + " evaluaciones");
        return id;
    }
}
