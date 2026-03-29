package com.epmapat.erp_epmapat.rrhh.servicio;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.epmapat.erp_epmapat.rrhh.modelo.ThAuditLog;
import com.epmapat.erp_epmapat.rrhh.repositorio.ThAuditLogR;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ThAuditServicio {

    private final ThAuditLogR dao;

    @Transactional
    public void log(String entidad, Long idregistro, String accion, String detalle, Long usuario) {
        ThAuditLog a = new ThAuditLog();
        a.setEntidad(entidad);
        a.setIdregistro(idregistro);
        a.setAccion(accion);
        a.setDetalle(detalle);
        a.setUsuario(usuario);
        dao.save(a);
    }

    @Transactional(readOnly = true)
    public List<ThAuditLog> byEntidad(String entidad) {
        return dao.findByEntidadOrderByFechaDesc(entidad);
    }

    @Transactional(readOnly = true)
    public List<ThAuditLog> byEntidadAndRegistro(String entidad, Long idregistro) {
        return dao.findByEntidadAndIdregistroOrderByFechaDesc(entidad, idregistro);
    }
}
