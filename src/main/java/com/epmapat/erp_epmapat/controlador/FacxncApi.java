package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.interfaces.NtaCreditoCompPago;
import com.epmapat.erp_epmapat.modelo.Facxnc;
import com.epmapat.erp_epmapat.servicio.FacxncService;

@RestController
@RequestMapping("/facxnc")
@CrossOrigin("*")
public class FacxncApi {
    @Autowired
    private FacxncService facxncService;

    @PostMapping
    public ResponseEntity<Facxnc> saveFacturaPorNotaCredito(@RequestBody Facxnc facxnc) {
        return ResponseEntity.ok(facxncService.save(facxnc));
    }

    @GetMapping("/factura")
    public ResponseEntity<List<NtaCreditoCompPago>> findByIdfactura(@RequestParam Long idfactura) {
        return ResponseEntity.ok(facxncService.findByIdfactura(idfactura));
    }
}
