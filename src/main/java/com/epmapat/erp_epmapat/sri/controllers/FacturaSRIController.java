package com.epmapat.erp_epmapat.sri.controllers;

import org.springframework.web.bind.annotation.*;

import com.epmapat.erp_epmapat.sri.exceptions.FacturaElectronicaException;
import com.epmapat.erp_epmapat.sri.models.Factura;
import com.epmapat.erp_epmapat.sri.repositories.FacturaR;
import com.epmapat.erp_epmapat.sri.services.FacturaSRIService;

import javax.persistence.Access;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/sri")
@CrossOrigin("*")
public class FacturaSRIController {
    @Autowired
    private FacturaR dao;

    private final FacturaSRIService facturaSRIService;

    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) {
        try {
            Factura factura = dao.findById(idfactura).orElseThrow(null);
            String xml = facturaSRIService.generarXmlFactura(factura);
            return ResponseEntity.ok(xml);
        } catch (FacturaElectronicaException e) {
            System.out.println("< ========= ERROR =========>");
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }
}