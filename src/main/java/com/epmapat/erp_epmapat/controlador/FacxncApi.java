package com.epmapat.erp_epmapat.controlador;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.modelo.Facxnc;
import com.epmapat.erp_epmapat.servicio.FacxncService;

@RestController
@RequestMapping("/facxnc")
@CrossOrigin("*")
public class FacxncApi {
    @Autowired
    private FacxncService facxncService;
@PostMapping
public ResponseEntity<Facxnc> saveFacturaPorNotaCredito(@RequestBody Facxnc facxnc){
    return ResponseEntity.ok(facxncService.save(facxnc));
}
}
