package com.epmapat.erp_epmapat.controlador;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

public class ValoresncApi {
    @org.springframework.beans.factory.annotation.Value("${erp.nc-endosos.enabled:false}") private boolean endososEnabled;
    @Autowired private com.epmapat.erp_epmapat.nc.NcSchemaState ncSchema;
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
        if(endososEnabled || ncSchema.migrated()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Usar cobro transaccional del backend");
        return ResponseEntity.ok(valoresServicio.save(valoresnc));
    }
}
