package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.epmapat.erp_epmapat.repositorio.contabilidad.Tabla15R;
import com.itextpdf.html2pdf.HtmlConverter;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@Service
public class XmlToPdfService {
    @Autowired
    FacturaSRIService facturaSRIService;
    @Autowired
    private Tabla15R tabla15r;

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
            // Utilidad para manejo seguro de BigDecimal
            Function<String, BigDecimal> safeBigDecimal = value -> {
                try {
                    return new BigDecimal(value == null || value.isEmpty() ? "0" : value);
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            };

            // Parseo del XML
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlAutorizado));
            inputSource.setEncoding("UTF-8");
            Document document = builder.parse(inputSource);

            if (document == null) {
                throw new RuntimeException("El documento XML no se ha podido transformar.");
            }

            // Extraer datos generales
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
            String nombreComercial = getNodeText(document, "nombreComercial");
            String obligadoContabilidad = getNodeText(document, "obligadoContabilidad");
            String contribuyenteEspecial = getNodeText(document, "contribuyenteEspecial");
            String nroFactura = getNodeText(document, "estab") + "-" + getNodeText(document, "ptoEmi") + "-"
                    + getNodeText(document, "secuencial");
            String ambiente = getNodeText(document, "ambiente");
            String razonSocialComprador = getNodeText(document, "razonSocialComprador");
            String identificacionComprador = getNodeText(document, "identificacionComprador");
            String direccionComprador = getNodeText(document, "direccionComprador");
            String guiaRemision = getNodeText(document, "guiaRemision"); // Asumido
            String formaPago = tabla15r.getNombre(getNodeText(document, "formaPago"));
            String totalDescuento = getNodeText(document, "totalDescuento");
            String propina = getNodeText(document, "propina");

            // Procesar items
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

            // Procesar impuestos
            NodeList impuestos = document.getElementsByTagName("totalImpuesto");
            BigDecimal subtotalIVA15 = BigDecimal.ZERO;
            BigDecimal subtotalIVA12 = BigDecimal.ZERO;
            BigDecimal subtotalIVA0 = BigDecimal.ZERO;
            BigDecimal subtotalNoObjetoIVA = BigDecimal.ZERO;
            BigDecimal subtotalExentoIVA = BigDecimal.ZERO;
            BigDecimal totalIVA15 = BigDecimal.ZERO;
            BigDecimal totalIVA12 = BigDecimal.ZERO;
            BigDecimal totalICE = BigDecimal.ZERO;
            BigDecimal totalIRBPNR = BigDecimal.ZERO;

            for (int i = 0; i < impuestos.getLength(); i++) {
                Element impuesto = (Element) impuestos.item(i);
                String codigo = getChildText(impuesto, "codigo");
                String codigoPorcentaje = getChildText(impuesto, "codigoPorcentaje");
                BigDecimal baseImponible = safeBigDecimal.apply(getChildText(impuesto, "baseImponible"));
                BigDecimal valor = safeBigDecimal.apply(getChildText(impuesto, "valor"));

                if ("2".equals(codigoPorcentaje)) {
                    subtotalIVA12 = subtotalIVA12.add(baseImponible);
                    totalIVA12 = totalIVA12.add(valor);
                } else if ("3".equals(codigoPorcentaje) || "4".equals(codigoPorcentaje)) {
                    subtotalIVA15 = subtotalIVA15.add(baseImponible);
                    totalIVA15 = totalIVA15.add(valor);
                } else if ("0".equals(codigoPorcentaje)) {
                    subtotalIVA0 = subtotalIVA0.add(baseImponible);
                } else if ("6".equals(codigoPorcentaje)) {
                    subtotalNoObjetoIVA = subtotalNoObjetoIVA.add(baseImponible);
                } else if ("7".equals(codigoPorcentaje)) {
                    subtotalExentoIVA = subtotalExentoIVA.add(baseImponible);
                }

                if ("3".equals(codigo)) {
                    totalICE = totalICE.add(valor);
                } else if ("5".equals(codigo)) {
                    totalIRBPNR = totalIRBPNR.add(valor);
                }
            }

            // Información adicional
            NodeList infoAdicional = document.getElementsByTagName("campoAdicional");
            Map<String, Object> parameters = new HashMap<>();
            for (int i = 0; i < infoAdicional.getLength(); i++) {
                Element campo = (Element) infoAdicional.item(i);
                String nombre = campo.getAttribute("nombre");
                String valor = campo.getTextContent();
                parameters.put(nombre, valor);
            }

            // Parámetros para Jasper
            parameters.put("RazonSocial", razonSocial);
            parameters.put("Ruc", ruc);
            parameters.put("NumeroAutorizacion", numeroAutorizacion);
            parameters.put("FechaAutorizacion", fechaAutorizacion);
            parameters.put("FechaEmision", fechaEmision);
            parameters.put("TotalSinImpuestos", totalSinImpuestos);
            parameters.put("DireccionMatriz", direccionMatriz);
            parameters.put("DireccionEstablecimiento", direccionEstablecimiento);
            parameters.put("Telefono", telefono);
            parameters.put("NombreComercial", nombreComercial);
            parameters.put("ObligadoContabilidad", obligadoContabilidad);
            parameters.put("ContribuyenteEspecial", contribuyenteEspecial);
            parameters.put("NroFactura", nroFactura);
            parameters.put("Ambiente", ambiente);
            parameters.put("AgenteRetencion", "00000001"); // Fijo o configurable
            parameters.put("RazonSocialComprador", razonSocialComprador);
            parameters.put("IdentificacionComprador", identificacionComprador);
            parameters.put("DireccionComprador", direccionComprador);
            parameters.put("GuiaRemision", guiaRemision);
            parameters.put("FormaPago", formaPago);
            parameters.put("TotalDescuento", safeBigDecimal.apply(totalDescuento));
            parameters.put("Propina", safeBigDecimal.apply(propina));
            parameters.put("ImporteTotal", safeBigDecimal.apply(importeTotal));

            // Subtotales e impuestos
            parameters.put("SubTotalIVA15", subtotalIVA15);
            parameters.put("SubTotalIVA12", subtotalIVA12);
            parameters.put("SubTotalIVA0", subtotalIVA0);
            parameters.put("SubTotalNoObjetoIVA", subtotalNoObjetoIVA);
            parameters.put("SubTotalExentoIVA", subtotalExentoIVA);
            parameters.put("TotalIVA15", totalIVA15);
            parameters.put("TotalIVA12", totalIVA12);
            parameters.put("TotalICE", totalICE);
            parameters.put("TotalIRBPNR", totalIRBPNR);

            // Compilar y llenar reporte
            InputStream reportStream = getClass().getResourceAsStream("/reports/factura_template.jrxml");
            if (reportStream == null) {
                throw new RuntimeException("Plantilla factura_template.jrxml no encontrada");
            }

            JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
            JRDataSource itemsDataSource = new JRBeanCollectionDataSource(itemsList);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, itemsDataSource);

            ByteArrayOutputStream pdfStream = new ByteArrayOutputStream();
            JasperExportManager.exportReportToPdfStream(jasperPrint, pdfStream);
            return pdfStream;

        } catch (Exception e) {
            System.err.println("Error al generar PDF: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error al generar PDF", e);
        }
    }

    public ByteArrayOutputStream ___generarFacturaPDF(String xmlAutorizado) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            // Document document = builder.parse(new
            // ByteArrayInputStream(xmlAutorizado.getBytes()));
            /*
             * Document document = builder.parse(
             * new ByteArrayInputStream(xmlAutorizado.getBytes(StandardCharsets.UTF_8)));
             */
            InputSource inputSource = new InputSource(new StringReader(xmlAutorizado));
            inputSource.setEncoding("UTF-8");
            Document document = builder.parse(inputSource);

            if (document != null) {
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
                // String correoElectronico = getNodeText(document, "Email");
                String nombreComercial = getNodeText(document, "nombreComercial");
                String obligadoContabilidad = getNodeText(document, "obligadoContabilidad");
                String contribuyenteEspecial = getNodeText(document, "contribuyenteEspecial");
                String nroFactura = getNodeText(document, "estab") + "-" + getNodeText(document, "ptoEmi") + "-"
                        + getNodeText(document, "secuencial");
                String ambiente = getNodeText(document, "ambiente");
                String razonSocialComprador = getNodeText(document, "razonSocialComprador");
                String identificacionComprador = getNodeText(document, "identificacionComprador");
                String direccionComprador = getNodeText(document, "direccionComprador");
                String giaRemision = getNodeText(document, "");
                String formaPago = tabla15r.getNombre(getNodeText(document, "formaPago"));
                String total = getNodeText(document, "total");
                String totalDescuento = getNodeText(document, "totalDescuento");
                String propina = getNodeText(document, "propina");

                // Extraer items de la factura
                NodeList items = document.getElementsByTagName("detalle");
                NodeList infoAdicional = document.getElementsByTagName("campoAdicional");
                Map<String, Object> parameters = new HashMap<>();

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
                /* calculo informacion tributaria */
                NodeList impuestos = document.getElementsByTagName("totalImpuesto");
                BigDecimal subtotalIVA15 = BigDecimal.ZERO;
                BigDecimal subtotalIVA0 = BigDecimal.ZERO;
                BigDecimal subtotalNoObjetoIVA = BigDecimal.ZERO;
                BigDecimal subtotalExentoIVA = BigDecimal.ZERO;
                BigDecimal totalICE = BigDecimal.ZERO;
                BigDecimal totalIRBPNR = BigDecimal.ZERO;
                BigDecimal totalIVA15 = BigDecimal.ZERO;
                BigDecimal totalIVA12 = BigDecimal.ZERO;
                BigDecimal subtotalIVA12 = BigDecimal.ZERO;

                for (int i = 0; i < impuestos.getLength(); i++) {
                    Element impuesto = (Element) impuestos.item(i);
                    String codigo = getChildText(impuesto, "codigo");
                    String codigoPorcentaje = getChildText(impuesto, "codigoPorcentaje");
                    BigDecimal baseImponible = new BigDecimal(getChildText(impuesto, "baseImponible"));
                    BigDecimal valor = new BigDecimal(getChildText(impuesto, "valor"));

                    if (codigo.equals("4")) {
                        switch (codigoPorcentaje) {
                            case "0":
                                subtotalIVA0 = subtotalIVA0.add(baseImponible);
                                System.out.println("subtotalIVA0" + subtotalIVA0);
                                break;
                            case "6":
                                subtotalNoObjetoIVA = subtotalNoObjetoIVA.add(baseImponible);
                                System.out.println("subtotalNoObjetoIVA" + subtotalNoObjetoIVA);

                                break;
                            case "7":
                                subtotalExentoIVA = subtotalExentoIVA.add(baseImponible);
                                System.out.println("subtotalExentoIVA" + subtotalExentoIVA);

                                break;
                            case "3":
                            case "4": // depende si usas IVA 12% o 15%
                                subtotalIVA15 = subtotalIVA15.add(baseImponible);
                                totalIVA15 = totalIVA15.add(valor);
                                System.out.println("totalIVA15" + totalIVA15);

                                break;
                            case "2": // depende si usas IVA 12% o 15%
                                subtotalIVA15 = subtotalIVA12.add(baseImponible);
                                totalIVA15 = totalIVA12.add(valor);
                                break;
                        }
                    } else if (codigo.equals("3")) {
                        totalICE = totalICE.add(valor);
                    } else if (codigo.equals("5")) {
                        totalIRBPNR = totalIRBPNR.add(valor);
                    }
                    System.out.println("valor: " + valor);
                    System.out.println("codigoPorcentaje: " + codigoPorcentaje);
                    System.out.println("baseImponible: " + baseImponible);
                }

                /*
                 * NodeList detalles = document.getElementsByTagName("detalle");
                 * BigDecimal totalDescuento = BigDecimal.ZERO;
                 * 
                 * for (int i = 0; i < detalles.getLength(); i++) {
                 * Element detalle = (Element) detalles.item(i);
                 * String descuentoStr = getChildText(detalle, "descuento");
                 * BigDecimal descuento = new BigDecimal(descuentoStr == null ||
                 * descuentoStr.isEmpty() ? "0" : descuentoStr);
                 * totalDescuento = totalDescuento.add(descuento);
                 * }
                 */
                // Propina
                /*
                 * String propina = getNodeText(document, "propina");
                 * 
                 * // Importe total
                 * String total = getNodeText(document, "importeTotal");
                 */

                // Extraer INFORMACION ADICIONAL
                // List<Map<String, String>> infoAdicionalList = new ArrayList<>();
                for (int i = 0; i < infoAdicional.getLength(); i++) {
                    Node itemNode = infoAdicional.item(i);
                    if (itemNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element itemElement = (Element) itemNode;
                        Map<String, String> item = new HashMap<>();
                        String nombre = itemElement.getAttribute("nombre");
                        String valor = itemElement.getTextContent();
                        // System.out.println(nombre + " - " + valor);
                        item.put(nombre, valor);
                        // item.put("Valor", valor);
                        parameters.put(nombre, valor);
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
                // Preparar parámetros y datos
                parameters.put("RazonSocial", razonSocial);
                parameters.put("Ruc", ruc);
                parameters.put("NumeroAutorizacion", numeroAutorizacion);
                parameters.put("FechaAutorizacion", fechaAutorizacion);
                parameters.put("FechaEmision", fechaEmision);
                parameters.put("TotalSinImpuestos", totalSinImpuestos);
                // parameters.put("ImporteTotal", importeTotal);
                parameters.put("DireccionMatriz", direccionMatriz);
                parameters.put("DireccionEstablecimiento", direccionEstablecimiento);
                parameters.put("Telefono", telefono);
                // parameters.put("CorreoElectronico", correoElectronico);
                parameters.put("NombreComercial", nombreComercial);
                parameters.put("ObligadoContabilidad", obligadoContabilidad);
                parameters.put("ContribuyenteEspecial", contribuyenteEspecial);
                parameters.put("NroFactura", nroFactura);
                parameters.put("Ambiente", ambiente);
                parameters.put("AgenteRetencion", "00000001");
                parameters.put("RazonSocialComprador", razonSocialComprador);
                parameters.put("IdentificacionComprador", identificacionComprador);
                parameters.put("DireccionComprador", direccionComprador);
                parameters.put("GuiaRemision", giaRemision);
                parameters.put("FormaPago", formaPago);
                parameters.put("Total", total);
                parameters.put("TotalDescuento", totalDescuento);
                parameters.put("Propina", propina);

                parameters.put("SubTotalIVA15", subtotalIVA15);
                parameters.put("SubTotalIVA12", subtotalIVA12);
                parameters.put("SubTotalIVA0", subtotalIVA0);
                parameters.put("SubTotalNoObjetoIVA", subtotalNoObjetoIVA);
                parameters.put("SubTotalExentoIVA", subtotalExentoIVA);
                parameters.put("TotalIVA15", totalIVA15);
                parameters.put("TotalIVA12", totalIVA12);
                parameters.put("TotalICE", totalICE);
                parameters.put("TotalIRBPNR", totalIRBPNR);
                parameters.put("Propina", new BigDecimal(propina == null ? "0" : propina));
                parameters.put("ImporteTotal", new BigDecimal(total));
                System.out.println("=======================");
                System.out.println("SubTotalIVA15 " + subtotalIVA15);
                System.out.println("SubTotalIVA12 " + subtotalIVA12);
                System.out.println("SubTotalIVA0 " + subtotalIVA0);
                System.out.println("SubTotalNoObjetoIVA " + subtotalNoObjetoIVA);
                System.out.println("SubTotalExentoIVA " + subtotalExentoIVA);
                System.out.println("TotalIVA15 " + totalIVA15);
                System.out.println("TotalDescuento " + totalDescuento);

                System.out.println("TotalICE " + totalICE);
                System.out.println("TotalIRBPNR " + totalIRBPNR);
                System.out.println("Propina " + propina);
                System.out.println("ImporteTotal " + total);
                System.out.println("=======================");
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
