package com.epmapat.erp_epmapat.controlador;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.modelo.Fec_factura_pagos;
import com.epmapat.erp_epmapat.servicio.Fec_factura_pagosService;

@RestController
@RequestMapping("/facturapagos")
@CrossOrigin(origins = "*")
public class Fec_factura_pagosApi {
    @Autowired
    private Fec_factura_pagosService fecfpagosService;

    @PostMapping
    public ResponseEntity<Fec_factura_pagos> postMethodName(@RequestBody Fec_factura_pagos fecfpagos) {

        return ResponseEntity.ok(fecfpagosService.save(fecfpagos));
    }
    @GetMapping("/pagos")
    public ResponseEntity<List<Fec_factura_pagos>> findAll() {
        return ResponseEntity.ok(fecfpagosService.findAll());
    }
}
