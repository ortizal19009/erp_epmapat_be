package com.epmapat.erp_epmapat.rrhh.controlador;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.rrhh.modelo.ThEmployeeFile;
import com.epmapat.erp_epmapat.rrhh.servicio.ThEmployeeFileServicio;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/th-files")
@RequiredArgsConstructor
public class ThEmployeeFileApi {

    private final ThEmployeeFileServicio service;

    @PostMapping
    public ResponseEntity<ThEmployeeFile> save(@RequestBody ThEmployeeFile f) {
        return ResponseEntity.ok(service.save(f));
    }

    @GetMapping("/persona/{idpersonal}")
    public ResponseEntity<List<ThEmployeeFile>> byPersonal(@PathVariable Long idpersonal) {
        return ResponseEntity.ok(service.byPersonal(idpersonal));
    }
}
