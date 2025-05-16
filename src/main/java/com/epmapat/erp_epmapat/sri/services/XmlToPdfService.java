package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.itextpdf.html2pdf.HtmlConverter;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperPrint;
import java.io.ByteArrayOutputStream;

@Service
public class XmlToPdfService {
    @Autowired
    FacturaSRIService facturaSRIService;

    public void generarPdfDesdeXml(String xmlContent, String pdfPath) throws Exception {
        // Ruta temporal para el HTML generado
        String htmlPath = "tempFactura.html";

        // Generar HTML a partir del XML
        String htmlContent = convertirXmlAHtml(xmlContent);

        // Guardar el HTML temporalmente
        try (FileWriter writer = new FileWriter(htmlPath)) {
            writer.write(htmlContent);
        }

        // Convertir el HTML a PDF
        HtmlConverter.convertToPdf(new FileInputStream(htmlPath), new FileOutputStream(pdfPath));

        // Borrar el archivo HTML temporal
        new File(htmlPath).delete();
    }

    private String convertirXmlAHtml(String xmlContent) {
        // Aquí puedes hacer una transformación XSLT o un simple reemplazo para armar el
        // HTML.
        return "<html><body><h1>Factura Electrónica</h1><p>" + xmlContent + "</p></body></html>";
    }

    public ByteArrayOutputStream generarFacturaPDF(String xmlAutorizado) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlAutorizado.getBytes()));

            if (document != null) {
                System.out.println("El documento XML se ha transformado correctamente.");

                // Extraer datos esenciales del XML
                String razonSocial = getNodeText(document, "razonSocial");
                String ruc = getNodeText(document, "ruc");
                String numeroAutorizacion = getNodeText(document, "numeroAutorizacion");
                String fechaAutorizacion = getNodeText(document, "fechaAutorizacion");

                String fechaEmision = getNodeText(document, "fechaEmision");
                String totalSinImpuestos = getNodeText(document, "totalSinImpuestos");
                String importeTotal = getNodeText(document, "importeTotal");
                String direccionMatriz = getNodeText(document, "dirMatriz");
                String direccionEstablecimiento = getNodeText(document, "dirEstablecimiento");
                String telefono = getNodeText(document, "telefono");
                String correoElectronico = getNodeText(document, "Email");
                String nombreComercial = getNodeText(document, "nombreComercial");
                String obligadoContabilidad = getNodeText(document, "obligadoContabilidad");
                String contribuyenteEspecial = getNodeText(document, "contribuyenteEspecial");
                String nroFactura = getNodeText(document, "estab") + "-" + getNodeText(document, "ptoEmi") + "-"
                        + getNodeText(document, "secuencial");
                String ambiente = getNodeText(document, "ambiente");

                // Extraer items de la factura
                NodeList items = document.getElementsByTagName("detalle");
                List<Map<String, String>> itemsList = new ArrayList<>();
                for (int i = 0; i < items.getLength(); i++) {
                    Node itemNode = items.item(i);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element itemElement = (Element) itemNode;
                        Map<String, String> item = new HashMap<>();
                        item.put("Codigo", getChildText(itemElement, "codigoPrincipal"));
                        item.put("Descripcion", getChildText(itemElement, "descripcion"));
                        item.put("Cantidad", getChildText(itemElement, "cantidad"));
                        item.put("PrecioUnitario", getChildText(itemElement, "precioUnitario"));
                        item.put("PrecioTotalSinImpuesto", getChildText(itemElement, "precioTotalSinImpuesto"));

                        itemsList.add(item);
                    }
                }

                // Cargar y compilar el reporte
                InputStream reportStream = getClass().getResourceAsStream("/reports/factura_template.jrxml");
                if (reportStream == null) {
                    System.out.println("No encontrado");
                    throw new RuntimeException("Plantilla factura_template.jrxml no encontrada");
                } else {
                    System.out.println("Si encontrado");
                }
                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
                System.out.println("NumeroAutorizacion: " + numeroAutorizacion);

                // Preparar parámetros y datos
                Map<String, Object> parameters = new HashMap<>();
                parameters.put("RazonSocial", razonSocial);
                parameters.put("Ruc", ruc);
                parameters.put("NumeroAutorizacion", numeroAutorizacion);
                parameters.put("FechaAutorizacion", fechaAutorizacion);
                parameters.put("FechaEmision", fechaEmision);
                parameters.put("TotalSinImpuestos", totalSinImpuestos);
                parameters.put("ImporteTotal", importeTotal);
                parameters.put("DireccionMatriz", direccionMatriz);
                parameters.put("DireccionEstablecimiento", direccionEstablecimiento);
                parameters.put("Telefono", telefono);
                parameters.put("CorreoElectronico", correoElectronico);
                parameters.put("NombreComercial", nombreComercial);
                parameters.put("ObligadoContabilidad", obligadoContabilidad);
                parameters.put("ContribuyenteEspecial", contribuyenteEspecial);
                parameters.put("NroFactura", nroFactura);
                parameters.put("Ambiente", ambiente);

                // Crear datasource para los items
                JRDataSource itemsDataSource = new JRBeanCollectionDataSource(itemsList);

                // Generar el PDF
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsDataSource);

                // Generar el PDF en un stream
                ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);

                return pdfStream;
            } else {
                System.out.println("El documento XML no se ha podido transformar.");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
        return null;
    }

    public ByteArrayOutputStream generar_FacturaPDF(String xmlAutorizado) {
        try {
            // Parseo del XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new ByteArrayInputStream(xmlAutorizado.getBytes()));

            if (document == null) {
                throw new RuntimeException("El documento XML no se ha podido transformar.");
            }

            // Extraer datos del XML
            String razonSocial = getNodeText(document, "razonSocial");
            String ruc = getNodeText(document, "ruc");
            String numeroAutorizacion = getNodeText(document, "numeroAutorizacion");
            String fechaEmision = getNodeText(document, "fechaEmision");
            String totalSinImpuestos = getNodeText(document, "totalSinImpuestos");
            String importeTotal = getNodeText(document, "importeTotal");

            // Extraer items
            NodeList items = document.getElementsByTagName("detalle");
            List<Map<String, String>> itemsList = new ArrayList<>();
            for (int i = 0; i < items.getLength(); i++) {
                Element itemElement = (Element) items.item(i);
                Map<String, String> item = new HashMap<>();
                item.put("Codigo", getChildText(itemElement, "codigoPrincipal"));
                item.put("Descripcion", getChildText(itemElement, "descripcion"));
                item.put("Cantidad", getChildText(itemElement, "cantidad"));
                item.put("PrecioUnitario", getChildText(itemElement, "precioUnitario"));
                item.put("PrecioTotalSinImpuesto", getChildText(itemElement, "precioTotalSinImpuesto"));
                itemsList.add(item);
            }

            // Cargar y compilar el reporte
            try (InputStream reportStream = getClass().getResourceAsStream("/reports/factura_template.jrxml")) {
                if (reportStream == null) {
                    throw new RuntimeException("Plantilla factura_template.jrxml no encontrada en /reports/");
                }

                JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);

                // Preparar parámetros
                Map<String, Object> parameters = new HashMap<>(Map.of(
                        "RazonSocial", razonSocial,
                        "Ruc", ruc,
                        "NumeroAutorizacion", numeroAutorizacion,
                        "FechaEmision", fechaEmision,
                        "TotalSinImpuestos", totalSinImpuestos,
                        "ImporteTotal", importeTotal));

                // DataSource
                JRDataSource itemsDataSource = new JRBeanCollectionDataSource(itemsList);

                // Generar el PDF
                JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsDataSource);

                // Retornar el PDF en un ByteArrayOutputStream
                ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
                JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);

                return pdfStream;
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al generar PDF: " + e.getMessage(), e);
        }
    }

    // Helper methods for XML parsing
    private String getNodeText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

    private String getChildText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

}
