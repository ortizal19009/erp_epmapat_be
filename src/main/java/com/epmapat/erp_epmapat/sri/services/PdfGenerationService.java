package com.epmapat.erp_epmapat.sri.services;

import com.epmapat.erp_epmapat.sri.services.parser.XmlParser;
import com.epmapat.erp_epmapat.sri.services.extractor.EcuadorianInvoiceExtractor;
import com.epmapat.erp_epmapat.sri.services.validator.InvoiceValidator;
import com.epmapat.erp_epmapat.sri.services.generator.PdfGenerator;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import java.io.IOException;
import java.util.Map;

@Service
public class PdfGenerationService {

    public static byte[] generatePdfFromXml(byte[] xmlBytes) {
        try {
            System.out.println("Paso 1: Parseando el XML...");
            Document document = XmlParser.parseXmlFromBytes(xmlBytes);
            System.out.println("Paso 2: Extrayendo información de la factura...");
            Map<String, String> facturaData = EcuadorianInvoiceExtractor.extractData(document);
            System.out.println("Datos extraídos: " + facturaData);

            System.out.println("Paso 3: Validando datos...");
            InvoiceValidator.validate(facturaData);

            System.out.println("Paso 4: Generando PDF...");
            return PdfGenerator.generate(facturaData);
        } catch (XmlParser.XmlParseException e) {
            System.err.println("Error al parsear el XML: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.err.println("Error de validación: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error al generar el PDF: " + e.getMessage());
        }
        return null;
    }
}