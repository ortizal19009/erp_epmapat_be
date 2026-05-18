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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
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
            // 1. Limpiar XML antes de parsear
            Function<String, BigDecimal> safeBigDecimal = value -> {
                try {
                    return new BigDecimal(value == null || value.isEmpty() ? "0" : value);
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            };

            ParsedAuthorizedXml parsed = parseAuthorizedXmlData(xmlAutorizado);
            Document document = parsed.documentoComprobante;
            String numeroAutorizacion = parsed.numeroAutorizacion;
            String fechaAutorizacion = parsed.fechaAutorizacion;
            String claveAcceso = safeValue(parsed.claveAcceso, getNodeText(document, "claveAcceso"));

            // Validar documento
            if (document == null) {
                throw new RuntimeException("El documento XML no se ha podido transformar.");
            }

            String fechaEmision = getNodeText(document, "fechaEmision");
            // Continuar extrayendo datos como en tu implementación...
            String razonSocial = getNodeText(document, "razonSocial");
            String ruc = getNodeText(document, "ruc");

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
            String guiaRemision = getNodeText(document, "guiaRemision");
            String formaPago = tabla15r.getNombre(getNodeText(document, "formaPago"));
            String totalDescuento = getNodeText(document, "totalDescuento");
            String propina = getNodeText(document, "propina");
            // String referencia = getNodeText(document, "referencia");
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

            // Parámetros Jasper
            parameters.put("RazonSocial", razonSocial);
            parameters.put("Ruc", ruc);
            parameters.put("NumeroAutorizacion", safeValue(numeroAutorizacion, "0000000000"));
            parameters.put("FechaAutorizacion", fechaAutorizacion);
            parameters.put("ClaveAcceso", claveAcceso);
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
            parameters.put("AgenteRetencion", "00000001");
            parameters.put("RazonSocialComprador", razonSocialComprador);
            parameters.put("IdentificacionComprador", identificacionComprador);
            parameters.put("DireccionComprador", direccionComprador);
            parameters.put("GuiaRemision", safeValue(guiaRemision, "NO APLICA"));
            parameters.put("FormaPago", formaPago);
            parameters.put("TotalDescuento", safeBigDecimal.apply(totalDescuento));
            parameters.put("Propina", safeBigDecimal.apply(propina));
            parameters.put("ImporteTotal", safeBigDecimal.apply(importeTotal));
            parameters.put("SubTotalIVA15", subtotalIVA15);
            parameters.put("SubTotalIVA12", subtotalIVA12);
            parameters.put("SubTotalIVA0", subtotalIVA0);
            parameters.put("SubTotalNoObjetoIVA", subtotalNoObjetoIVA);
            parameters.put("SubTotalExentoIVA", subtotalExentoIVA);
            parameters.put("TotalIVA15", totalIVA15);
            parameters.put("TotalIVA12", totalIVA12);
            parameters.put("TotalICE", totalICE);
            parameters.put("TotalIRBPNR", totalIRBPNR);
            // parameters.put("Referencia", "3333");

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

    private String limpiarXml(String xml) {
        if (xml == null)
            return "";
        return xml
                .replaceAll("﻿", "") // Eliminar BOM
                .replaceAll("^\\s+", "") // Eliminar espacios/saltos antes del prolog
                .replaceAll("[^\\x09\\x0A\\x0D\\x20-\\x7E]", ""); // Eliminar caracteres invisibles
    }

    // Función de utilidad para evitar valores nulos o vacíos
    private String safeValue(String value, String defaultVal) {
        return (value == null || value.trim().isEmpty()) ? defaultVal : value;
    }

    public ByteArrayOutputStream generarFacturaPDF_v2(String xmlAutorizado) {
        try {
            // Utilidad para manejo seguro de BigDecimal
            Function<String, BigDecimal> safeBigDecimal = value -> {
                try {
                    return new BigDecimal(value == null || value.isEmpty() ? "0" : value);
                } catch (Exception e) {
                    return BigDecimal.ZERO;
                }
            };

            ParsedAuthorizedXml parsed = parseAuthorizedXmlData(xmlAutorizado);
            Document document = parsed.documentoComprobante;
            if (document == null) {
                throw new RuntimeException("El documento XML no se ha podido transformar.");
            }

            // Extraer datos generales
            String razonSocial = getNodeText(document, "razonSocial");
            String ruc = getNodeText(document, "ruc");
            String numeroAutorizacion = parsed.numeroAutorizacion;
            String fechaAutorizacion = parsed.fechaAutorizacion;
            String claveAcceso = safeValue(parsed.claveAcceso, getNodeText(document, "claveAcceso"));
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
            parameters.put("NumeroAutorizacion", safeValue(numeroAutorizacion, "0000000000"));
            parameters.put("FechaAutorizacion", fechaAutorizacion);
            parameters.put("ClaveAcceso", claveAcceso);
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
            parameters.put("GuiaRemision", safeValue(guiaRemision, "NO APLICA"));
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

    // Helper methods for XML parsing
    private String getNodeText(Document doc, String tagName) {
        NodeList nodes = doc.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

    private String getChildText(Element element, String tagName) {
        NodeList nodes = element.getElementsByTagName(tagName);
        return nodes.getLength() > 0 ? nodes.item(0).getTextContent() : "";
    }

    private Document parseAuthorizedXml(String xmlAutorizado) throws Exception {
        return parseAuthorizedXmlData(xmlAutorizado).documentoComprobante;
    }

    private ParsedAuthorizedXml parseAuthorizedXmlData(String xmlAutorizado) throws Exception {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();

        String xmlLimpio = normalizarXmlInterno(xmlAutorizado);
        Document document;
        try {
            document = parseXml(builder, xmlLimpio);
        } catch (Exception ex) {
            String extraido = extraerPrimerXml(xmlLimpio);
            if (extraido == null || extraido.isBlank()) {
                System.err.println("XML no parseable. Inicio recibido: " + resumirInicio(xmlLimpio));
                throw ex;
            }
            document = parseXml(builder, extraido);
        }

        ParsedAuthorizedXml parsed = new ParsedAuthorizedXml();
        parsed.numeroAutorizacion = safeValue(getNodeText(document, "numeroAutorizacion"), "");
        parsed.fechaAutorizacion = safeValue(getNodeText(document, "fechaAutorizacion"), "");
        parsed.claveAcceso = safeValue(getNodeText(document, "claveAcceso"), "");

        NodeList comprobanteNodes = document.getElementsByTagName("comprobante");
        if (comprobanteNodes.getLength() == 0) {
            parsed.documentoComprobante = document;
            return parsed;
        }

        String comprobanteContenido = normalizarXmlInterno(comprobanteNodes.item(0).getTextContent());
        if (comprobanteContenido.isBlank()) {
            parsed.documentoComprobante = document;
            return parsed;
        }

        String xmlInterno = normalizarXmlInterno(comprobanteContenido);
        Document documentoComprobante = parseXml(builder, xmlInterno);
        parsed.documentoComprobante = documentoComprobante;
        if (parsed.claveAcceso == null || parsed.claveAcceso.isBlank()) {
            parsed.claveAcceso = safeValue(getNodeText(documentoComprobante, "claveAcceso"), "");
        }
        return parsed;
    }

    private Document parseXml(DocumentBuilder builder, String xml) throws Exception {
        InputSource inputSource = new InputSource(new StringReader(xml));
        inputSource.setEncoding("UTF-8");
        return builder.parse(inputSource);
    }

    private String limpiarXmlRobusto(String xml) {
        if (xml == null) {
            return "";
        }

        String limpio = xml
                .replace("\uFEFF", "")
                .replace("ï»¿", "")
                .replace("\u0000", "")
                .trim();
        limpio = limpio.replaceAll("^(ï»¿|Ã¯Â»Â¿)+", "");

        int inicioXml = limpio.indexOf('<');
        if (inicioXml > 0) {
            limpio = limpio.substring(inicioXml);
        }

        return limpio.trim();
    }

    private String normalizarXmlInterno(String xml) {
        String normalizado = limpiarXmlRobusto(xml);

        if (!normalizado.startsWith("<")) {
            normalizado = unescapeXmlBasico(normalizado);
            normalizado = limpiarXmlRobusto(normalizado);
        }

        Pattern pattern = Pattern.compile("(?is)<\\?xml.*|<factura\\b.*|<notaCredito\\b.*|<liquidacionCompra\\b.*|<autorizacion\\b.*");
        Matcher matcher = pattern.matcher(normalizado);
        if (matcher.find()) {
            if (matcher.start() > 0) {
                normalizado = normalizado.substring(matcher.start());
            }
            return normalizado;
        }
        String extraido = extraerPrimerXml(normalizado);
        return extraido == null || extraido.isBlank() ? normalizado : extraido;
    }

    private String unescapeXmlBasico(String value) {
        return value
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&quot;", "\"")
                .replace("&apos;", "'")
                .replace("&amp;", "&");
    }

    private String extraerPrimerXml(String valor) {
        String texto = limpiarXmlRobusto(unescapeXmlBasico(valor));
        Pattern[] patrones = new Pattern[] {
                Pattern.compile("(?is)(<\\?xml[^>]*\\?>.*)"),
                Pattern.compile("(?is)(<(factura|notaCredito|liquidacionCompra|autorizacion)\\b.*)")
        };

        for (Pattern patron : patrones) {
            Matcher matcher = patron.matcher(texto);
            if (matcher.find()) {
                String extraido = texto.substring(matcher.start(1)).trim();
                if (extraido.startsWith("<")) {
                    return extraido;
                }
            }
        }

        return null;
    }

    private String resumirInicio(String valor) {
        if (valor == null) {
            return "<null>";
        }
        String compacto = valor.replaceAll("\\s+", " ").trim();
        return compacto.length() <= 180 ? compacto : compacto.substring(0, 180) + "...";
    }

    private static class ParsedAuthorizedXml {
        private Document documentoComprobante;
        private String numeroAutorizacion;
        private String fechaAutorizacion;
        private String claveAcceso;
    }

}
