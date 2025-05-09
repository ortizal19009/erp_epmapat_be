package com.epmapat.erp_epmapat.sri.services.generator;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Map;

public class PdfGenerator {

    public static byte[] generate(Map<String, String> facturaData) throws IOException {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage();
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
                contentStream.beginText();
                contentStream.newLineAtOffset(50, 700);

                // Encabezado
                writePdfLine(contentStream, "FACTURA ELECTRÓNICA ECUATORIANA", PDType1Font.HELVETICA_BOLD, 14);
                writePdfLine(contentStream, "Número: " + facturaData.getOrDefault("secuencial", "N/A"));
                writePdfLine(contentStream, "Clave de acceso: " + facturaData.getOrDefault("claveAcceso", "N/A"));
                writePdfLine(contentStream, "Fecha emisión: " + facturaData.getOrDefault("fechaEmision", "N/A"));

                // Separador
                writePdfLine(contentStream, "----------------------------------------");

                // Información del emisor
                writePdfLine(contentStream, "EMISOR:", PDType1Font.HELVETICA_BOLD, 12);
                writePdfLine(contentStream, facturaData.getOrDefault("razonSocialEmisor", "N/A"));
                writePdfLine(contentStream, "RUC: " + facturaData.getOrDefault("rucEmisor", "N/A"));
                writePdfLine(contentStream, facturaData.getOrDefault("direccionEmisor", ""));

                // Información del comprador
                writePdfLine(contentStream, "COMPRADOR:", PDType1Font.HELVETICA_BOLD, 12);
                writePdfLine(contentStream, facturaData.getOrDefault("razonSocialComprador", "N/A"));
                writePdfLine(contentStream, "Identificación: " + facturaData.getOrDefault("identificacionComprador", "N/A"));

                // Totales
                writePdfLine(contentStream, "----------------------------------------");
                writePdfLine(contentStream, "SUBTOTAL: " + facturaData.getOrDefault("totalSinImpuestos", "N/A"));
                writePdfLine(contentStream, "IVA 12%: " + facturaData.getOrDefault("iva12", "N/A"));
                writePdfLine(contentStream, "TOTAL: " + facturaData.getOrDefault("importeTotal", "N/A"), PDType1Font.HELVETICA_BOLD, 12);

                contentStream.endText();
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.save(byteArrayOutputStream);
            return byteArrayOutputStream.toByteArray();
        }
    }

    private static void writePdfLine(PDPageContentStream contentStream, String text) throws IOException {
        writePdfLine(contentStream, text, PDType1Font.HELVETICA, 10);
    }

    private static void writePdfLine(PDPageContentStream contentStream, String text, PDType1Font font, int size) throws IOException {
        contentStream.setFont(font, size);
        contentStream.showText(text);
        contentStream.newLineAtOffset(0, -20);
    }

    /**
     * Modificación: Agregar el namespace al encabezado XML de la factura
     */
    private static String getXmlFactura(Map<String, String> facturaData) {
        StringBuilder xml = new StringBuilder();
        
        xml.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        xml.append("<factura id=\"comprobante\" version=\"1.0.0\" xmlns=\"http://www.sri.gob.ec/factura\">\n");
        
        // Resto del contenido XML de la factura
        xml.append("<infoEmisor>\n");
        xml.append("<razonSocial>").append(facturaData.getOrDefault("razonSocialEmisor", "N/A")).append("</razonSocial>\n");
        xml.append("<ruc>").append(facturaData.getOrDefault("rucEmisor", "N/A")).append("</ruc>\n");
        xml.append("</infoEmisor>\n");

        xml.append("<infoComprador>\n");
        xml.append("<razonSocial>").append(facturaData.getOrDefault("razonSocialComprador", "N/A")).append("</razonSocial>\n");
        xml.append("<identificacion>").append(facturaData.getOrDefault("identificacionComprador", "N/A")).append("</identificacion>\n");
        xml.append("</infoComprador>\n");

        xml.append("<totales>\n");
        xml.append("<subtotal>").append(facturaData.getOrDefault("totalSinImpuestos", "N/A")).append("</subtotal>\n");
        xml.append("<iva>").append(facturaData.getOrDefault("iva12", "N/A")).append("</iva>\n");
        xml.append("<total>").append(facturaData.getOrDefault("importeTotal", "N/A")).append("</total>\n");
        xml.append("</totales>\n");

        xml.append("</factura>");
        
        return xml.toString();
    }
}
