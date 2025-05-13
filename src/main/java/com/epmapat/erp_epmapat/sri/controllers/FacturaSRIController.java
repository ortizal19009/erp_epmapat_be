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

import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import java.io.File;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
            Factura factura = dao.findById(idfactura).orElseThrow(() -> new RuntimeException("Factura no encontrada"));
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

    @PostMapping("/generate-pdf-jasper")
    public ResponseEntity<byte[]> generatePdfWithJasper(
            @RequestParam("xmlFile") MultipartFile xmlFile,
            @RequestParam("jrxmlTemplate") MultipartFile jrxmlTemplate) throws Exception {

        // Cargar el diseño del reporte (.jrxml)
        InputStream reportStream = jrxmlTemplate.getInputStream();
        JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

        // Crear datasource desde el XML
        JRXmlDataSource xmlDataSource = new JRXmlDataSource(xmlFile.getInputStream(), "/ruta/nodo"); // Ajusta la ruta
                                                                                                     // XPath

        // Parámetros adicionales
        Map<String, Object> parameters = new HashMap<>();
        /*
         * parameters.put("CLAVE_ACCESO", obtenerClaveAcceso(xml)); // Extraer del XML
         * parameters.put("DIRECCION_MATRIZ", obtenerDireccionMatriz(xml));
         * parameters.put("DIRECCION_ESTABLECIMIENTO",
         * obtenerDireccionEstablecimiento(xml));
         * parameters.put("LOGO", "ruta/a/logo.jpg");
         */

        // Llenar el reporte
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, xmlDataSource);

        // Exportar a PDF
        byte[] pdfBytes = JasperExportManager.exportReportToPdf(jasperPrint);

        // Configurar respuesta HTTP
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename("reporte.pdf").build());

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}