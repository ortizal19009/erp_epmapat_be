package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.ThAuditLog;
import com.epmapat.erp_epmapat.rrhh.servicio.ThAuditServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/th-audit")
@RequiredArgsConstructor
public class ThAuditApi {

    private final ThAuditServicio service;

    @GetMapping
    public ResponseEntity<List<ThAuditLog>> get(
            @RequestParam String entidad,
            @RequestParam(required = false) Long idregistro) {
        if (idregistro == null) {
            return ResponseEntity.ok(service.byEntidad(entidad));
        }
        return ResponseEntity.ok(service.byEntidadAndRegistro(entidad, idregistro));
    }
}
