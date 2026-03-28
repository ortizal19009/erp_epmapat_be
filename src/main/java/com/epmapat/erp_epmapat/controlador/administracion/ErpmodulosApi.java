package com.epmapat.erp_epmapat.controlador.administracion;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.excepciones.ResourceNotFoundExcepciones;
import com.epmapat.erp_epmapat.modelo.administracion.Erpmodulos;
import com.epmapat.erp_epmapat.servicio.administracion.ErpmodulosServicio;

@RestController
@RequestMapping("/erpmodulos")

public class ErpmodulosApi {
    @Autowired
    private ErpmodulosServicio emServicio;

    @GetMapping
    public ResponseEntity<List<Erpmodulos>> getAll() {
        return ResponseEntity.ok(emServicio.findAll());
    }

    @GetMapping("/platform/{platform}")
    public ResponseEntity<List<Erpmodulos>> findByPlatform(@PathVariable String platform) {
        return ResponseEntity.ok(emServicio.findByPlatform(platform));
    }

    @PostMapping
    public ResponseEntity<Erpmodulos> save(@RequestBody Erpmodulos erpmodulos) {
        return ResponseEntity.ok(emServicio.save(erpmodulos));
    }

    @PutMapping("/{iderpmodulo}")
    public ResponseEntity<Erpmodulos> update(@PathVariable Long iderpmodulo,
            @RequestBody Erpmodulos erpmodulos,
            @RequestParam Long usumodi,
            @RequestParam(required = false, defaultValue = "MODIFICACION") String tipo,
            @RequestParam(required = false, defaultValue = "Actualización de módulo ERP") String observacion) {
        emServicio.findById(iderpmodulo)
                .orElseThrow(() -> new ResourceNotFoundExcepciones(
                        "No existe módulo ERP con Id: " + iderpmodulo));

        Erpmodulos updated = emServicio.actualizarErpmoduloConAuditoria(
                iderpmodulo, erpmodulos, usumodi, observacion, tipo);
        return ResponseEntity.ok(updated);
    }
}
