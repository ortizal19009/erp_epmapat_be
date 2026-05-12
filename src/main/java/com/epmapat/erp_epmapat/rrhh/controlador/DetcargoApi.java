package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.Detcargo;
import com.epmapat.erp_epmapat.rrhh.servicio.DetcargoServicio;

@RestController("rrhhLegacyDetcargoApi")
@RequestMapping("/api/detcargo")

public class DetcargoApi {
    @Autowired
    private DetcargoServicio detcargoServicio;

    @GetMapping
    public ResponseEntity<List<Detcargo>> getAll() {
        return ResponseEntity.ok(detcargoServicio.findAll());
    }

    @PostMapping
    public ResponseEntity<Detcargo> save(@RequestBody Detcargo detcargo) {
        return ResponseEntity.ok(detcargoServicio.save(detcargo));
    }
}

