package com.epmapat.erp_epmapat.sri.controllers;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;

import com.epmapat.erp_epmapat.modelo.administracion.Definir;
import com.epmapat.erp_epmapat.servicio.administracion.DefinirServicio;
import com.epmapat.erp_epmapat.sri.dto.EmailRequest;
import com.epmapat.erp_epmapat.sri.exceptions.FacturaElectronicaException;
import com.epmapat.erp_epmapat.sri.models.Factura;
import com.epmapat.erp_epmapat.sri.repositories.FacturaR;
import com.epmapat.erp_epmapat.sri.services.EmailService;
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
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
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
    private XmlSignerService xmlSignerService;
    @Autowired
    private DefinirServicio definirService;
    @Autowired
    private EmailService emailService;

    private final FacturaSRIService facturaSRIService;
    @Value("${xml.storage.path}")
    private String xmlStoragePath;

    public FacturaSRIController(FacturaSRIService facturaSRIService) {
        this.facturaSRIService = facturaSRIService;
    }

    @GetMapping("/generar-xml")
    public ResponseEntity<String> generarXmlFactura(@RequestParam Long idfactura) throws Exception {
        Definir definir = definirService.findById(1L).orElseThrow(() -> new RuntimeException("Definir no encontrado"));
        try {
            Factura factura = dao.findById(idfactura).orElseThrow(() -> new RuntimeException("Factura no encontrada"));
            if (factura == null) {
                return ResponseEntity.noContent().build();
            } else {
                String xml = facturaSRIService.generarXmlFactura(factura);
                String xmlFirmado = xmlSignerService.signXml(xml, definir.getFirma(), "Junior2012");
                FacturaSRIService.saveXml(xmlFirmado, ".//xmlFiles//Fac_" + factura.getEstablecimiento() + "-"
                        + factura.getPuntoemision() + "-" + factura.getSecuencial() + ".xml");
                return ResponseEntity.ok(xml);
            }
        } catch (

        FacturaElectronicaException e) {
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

    /*
     * @GetMapping("/firmar-xml")
     * public ResponseEntity<Object> firmarDoc(File xmlFile, File p12File, String
     * p12Password, String alias)
     * throws Exception {
     * Document doc = XmlSignerService.signXml(xmlFile, p12File, p12Password,
     * alias);
     * return ResponseEntity.ok(doc);
     * }
     */

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

    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMail(@RequestParam String emisor, @RequestParam String password, @RequestParam List<String> receptores, @RequestParam String asunto, @RequestParam String mensaje) {
        try {
            // Configuración del correo
            emisor = "facturacion@epmapatulcan.gob.ec";
            password = "79DB6F2BFA7FFED2E17F16CABA197D2063EB";
            receptores = List.of("ortizln9@gmail.com", "alexis.ortiz81@outlook.com",
                    "saulruales@gmail.com", "ortizln9@gmail.com");
            asunto = "Prueba mail facturas";
            mensaje = "<h1>ANUNCIO EPMAPA-T</h1><p>Este es un correo de prueba enviado desde el sistema.</p>";
            // Envío del correo
            boolean resultado = emailService.envioEmail(emisor, password, receptores, asunto, mensaje);

            // Respuesta estructurada
            Map<String, Object> response = new HashMap<>();
            response.put("success", resultado);
            response.put("message", resultado ? "Correo enviado exitosamente" : "Error al enviar el correo");
            response.put("timestamp", new Date());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "Error en el servidor: " + e.getMessage());
            errorResponse.put("timestamp", new Date());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}