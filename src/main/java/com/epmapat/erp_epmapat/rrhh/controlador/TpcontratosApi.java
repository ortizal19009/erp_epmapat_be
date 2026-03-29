package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.Tpcontratos;
import com.epmapat.erp_epmapat.rrhh.servicio.TpcontratosServicio;

@RestController("rrhhLegacyTpcontratosApi")
@RequestMapping("/api/tpcontratos")

public class TpcontratosApi {
    @Autowired
    private TpcontratosServicio tpcontratosServicio;

    @GetMapping
    public ResponseEntity<List<Tpcontratos>> getAll() {
        return ResponseEntity.ok(tpcontratosServicio.findAll());
    }

    @PostMapping
    public ResponseEntity<Tpcontratos> saveTpContrato(@RequestBody Tpcontratos tpcontratos) {
        return ResponseEntity.ok(tpcontratosServicio.save(tpcontratos));
    }
}

