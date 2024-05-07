package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Fec_factura_detalles_impuestos;
import com.epmapat.erp_epmapat.servicio.Fec_factura_detalles_impuestosServicio;

@RestController
@RequestMapping("/facdetallesimpuestos")
@CrossOrigin("*")
public class Fec_factura_detalles_impuestosApi {
    @Autowired
    private Fec_factura_detalles_impuestosServicio fecfdetimpService;

    @PostMapping
    public ResponseEntity<Fec_factura_detalles_impuestos> saveFacImpuestos(
            @RequestBody Fec_factura_detalles_impuestos fecfdetimp) {
        return ResponseEntity.ok(fecfdetimpService.save(fecfdetimp));

    }
}
