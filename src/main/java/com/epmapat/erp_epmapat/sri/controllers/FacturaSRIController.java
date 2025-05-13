package com.epmapat.erp_epmapat.sri.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.epmapat.erp_epmapat.sri.dto.EmailRequest;
import com.epmapat.erp_epmapat.sri.exceptions.FacturaElectronicaException;
import com.epmapat.erp_epmapat.sri.models.Factura;
import com.epmapat.erp_epmapat.sri.repositories.FacturaR;
import com.epmapat.erp_epmapat.sri.services.FacturaSRIService;
import com.epmapat.erp_epmapat.sri.services.XmlSignerService;

import java.io.File;
import java.net.http.HttpHeaders;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.tomcat.util.http.parser.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/sri")
@CrossOrigin("*")
public class FacturaSRIController {
    @Autowired
    private FacturaR dao;
    @Autowired
    private XmlSignerService XmlSignerService;

    private final FacturaSRIService facturaSRIService;
    @Value("${xml.storage.path}")
    private String xmlStoragePath;

    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) {
        try {
            Factura factura = dao.findById(idfactura).orElseThrow(null);
            String xml = facturaSRIService.generarXmlFactura(factura);
            FacturaSRIService.saveXml(xml, ".//xmlFiles//Factura_" + factura.getEstablecimiento() + "_"
                    + factura.getPuntoemision() + "_" + factura.getSecuencial() + ".xml");
            return ResponseEntity.ok(xml);
        } catch (FacturaElectronicaException e) {
            System.out.println("< ========= ERROR =========>");
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    @PostMapping(value = "/enviar", consumes = { "multipart/form-data" })
    public ResponseEntity<String> enviarFactura(
            @RequestParam String toEmail,
            @RequestParam String subject,
            @RequestParam String body,
            @RequestParam MultipartFile xmlFile) {
        System.out.println(xmlFile);

        try {
            facturaSRIService.processAndSendInvoice(toEmail, subject, body, xmlFile);
            return ResponseEntity.ok("Factura convertida a PDF y enviada por email exitosamente");
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Error al procesar la factura: " + e.getMessage());
        }
    }

    @GetMapping("/firmar-xml")
    public ResponseEntity<Object> firmarDoc(File xmlFile, File p12File, String p12Password, String alias)
            throws Exception {
        Document doc = XmlSignerService.signXml(xmlFile, p12File, p12Password, alias);
        return ResponseEntity.ok(doc);
    }

}