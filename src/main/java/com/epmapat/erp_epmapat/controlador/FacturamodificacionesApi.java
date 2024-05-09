package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Facturamodificaciones;
import com.epmapat.erp_epmapat.servicio.FacturamodificacionesServicio;

@RestController
@RequestMapping("/facturamodificaciones")
@CrossOrigin("*")
public class FacturamodificacionesApi {
    @Autowired
    private FacturamodificacionesServicio fmodiServicio;

    @GetMapping
    public ResponseEntity<List<Facturamodificaciones>> getAllFacturaModificaciones() {
        List<Facturamodificaciones> fmodificaciones  = fmodiServicio.findAll_modi();
        if(fmodificaciones != null){
            return ResponseEntity.ok(fmodificaciones);
        }
        return ResponseEntity.noContent().build();
    }
    @PostMapping
    public ResponseEntity<Facturamodificaciones> saveFacturaModificaciones(@RequestBody Facturamodificaciones facmodi){
        return ResponseEntity.ok(fmodiServicio.save_modi(facmodi));
    }

}
