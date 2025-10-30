package com.epmapat.erp_epmapat.controlador.rrhh;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.rrhh.Cargos;
import com.epmapat.erp_epmapat.servicio.rrhh.CargosServicio;

@RestController
@RequestMapping("/cargos")
@CrossOrigin("*")
public class CargosApi {
    @Autowired
    private CargosServicio cargosServicio;

    @GetMapping
    public ResponseEntity<List<Cargos>> getAll() {
        return ResponseEntity.ok(cargosServicio.findAll());
    }

    @PostMapping
    public ResponseEntity<Cargos> saveCargo(@RequestBody Cargos cargo) {
        return ResponseEntity.ok(this.cargosServicio.save(cargo));
    }
}
