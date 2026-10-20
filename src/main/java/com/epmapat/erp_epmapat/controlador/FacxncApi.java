package com.epmapat.erp_epmapat.controlador;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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

public class FacxncApi {
    @org.springframework.beans.factory.annotation.Value("${erp.nc-endosos.enabled:false}") private boolean endososEnabled;
    @Autowired private com.epmapat.erp_epmapat.nc.NcSchemaState ncSchema;
    @Autowired
    private FacxncService facxncService;

    @PostMapping
    public ResponseEntity<Facxnc> saveFacturaPorNotaCredito(@RequestBody Facxnc facxnc) {
        if(endososEnabled || ncSchema.migrated()) throw new org.springframework.web.server.ResponseStatusException(org.springframework.http.HttpStatus.CONFLICT,"Usar cobro transaccional del backend");
        return ResponseEntity.ok(facxncService.save(facxnc));
    }

    @GetMapping("/factura")
    public ResponseEntity<List<NtaCreditoCompPago>> findByIdfactura(@RequestParam Long idfactura) {
        return ResponseEntity.ok(facxncService.findByIdfactura(idfactura));
    }
}
