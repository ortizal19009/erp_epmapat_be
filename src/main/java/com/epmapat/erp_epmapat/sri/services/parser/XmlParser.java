package com.epmapat.erp_epmapat.sri.services.parser;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
@Service
public class XmlParser {

    public static Document parseXmlFromBytes(byte[] xmlBytes) throws XmlParseException {
        if (xmlBytes == null || xmlBytes.length == 0) {
            throw new XmlParseException("El XML en bytes está vacío o es nulo");
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

            // Protección contra ataques XXE
            factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
            factory.setFeature("http://xml.org/sax/features/external-general-entities", false);
            factory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);

            factory.setNamespaceAware(true);

            DocumentBuilder builder = factory.newDocumentBuilder();

            InputSource inputSource = new InputSource(new ByteArrayInputStream(xmlBytes));
            inputSource.setEncoding(StandardCharsets.UTF_8.name());

            Document document = builder.parse(inputSource);
            document.getDocumentElement().normalize();
            System.out.println("============> DOCUMENTO GENERADO CORRECTAMENTE <========================");
            return document;

        } catch (ParserConfigurationException e) {
            throw new XmlParseException("Error de configuración del parser XML", e);
        } catch (SAXException e) {
            throw new XmlParseException("Error al parsear el XML (formato inválido)", e);
        } catch (IOException e) {
            throw new XmlParseException("Error de IO al leer el XML", e);
        }
    }

    public static class XmlParseException extends Exception {
        public XmlParseException(String message) {
            super(message);
        }

        public XmlParseException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
