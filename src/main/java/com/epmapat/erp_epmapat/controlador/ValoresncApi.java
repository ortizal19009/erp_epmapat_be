package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Valoresnc;
import com.epmapat.erp_epmapat.servicio.ValoresncServicio;

@RestController
@RequestMapping("/valoresnc")
@CrossOrigin("*")
public class ValoresncApi {
    @Autowired
    private ValoresncServicio valoresServicio;
    
    
    @GetMapping
    public ResponseEntity<List<Valoresnc>> getAll() {
        return ResponseEntity.ok(valoresServicio.findAll());
    }

    @GetMapping("/{idvaloresnc}")
    public ResponseEntity<Optional<Valoresnc>> getById(@PathVariable Long idvaloresnc) {
        return ResponseEntity.ok(valoresServicio.findById(idvaloresnc));
    }

    @PostMapping
    public ResponseEntity<Valoresnc> save(@RequestBody Valoresnc valoresnc) {
        return ResponseEntity.ok(valoresServicio.save(valoresnc));
    }
}
