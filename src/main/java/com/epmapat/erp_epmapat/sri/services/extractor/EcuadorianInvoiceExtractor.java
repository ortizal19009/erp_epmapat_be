package com.epmapat.erp_epmapat.sri.services.extractor;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class EcuadorianInvoiceExtractor {

    public static Map<String, String> extractData(Document document) {
        Map<String, String> facturaData = new HashMap<>();

        facturaData.put("secuencial", getNodeValue(document, "secuencial"));
        facturaData.put("fechaEmision", getNodeValue(document, "fechaEmision|fecha"));
        facturaData.put("razonSocialEmisor", getNodeValue(document, "razonSocial|nombreComercial"));
        facturaData.put("rucEmisor", getNodeValue(document, "ruc|identificacion"));
        facturaData.put("direccionEmisor", getNodeValue(document, "direccion"));
        facturaData.put("razonSocialComprador", getNodeValue(document, "razonSocialComprador|nombreComprador"));
        facturaData.put("identificacionComprador", getNodeValue(document, "identificacionComprador"));
        facturaData.put("totalSinImpuestos", getNodeValue(document, "totalSinImpuestos|subtotal"));
        facturaData.put("importeTotal", getNodeValue(document, "importeTotal|total"));
        facturaData.put("iva12", getNodeValue(document, "iva12|totalIva"));
        facturaData.put("claveAcceso", getNodeValue(document, "claveAcceso|codDoc"));

        return facturaData;
    }

private static String getNodeValue(Document doc, String expression) {
    try {
        System.out.println("DOC: " + doc);
        System.out.println("Expression: "+ expression);
        XPathFactory xPathFactory = XPathFactory.newInstance();
        XPath xpath = xPathFactory.newXPath();

        // Registrar un NamespaceContext para manejar namespaces
        xpath.setNamespaceContext(new NamespaceContext() {
            @Override
            public String getNamespaceURI(String prefix) {
                if ("".equals(prefix)) {
                    return doc.getDocumentElement().getNamespaceURI();
                } else if ("ns1".equals(prefix)) {
                    return "http://namespace1.com"; // Reemplazar por el namespace real
                } else if ("ns2".equals(prefix)) {
                    return "http://namespace2.com"; // Reemplazar por el namespace real
                }
                return null;
            }

            @Override
            public String getPrefix(String namespaceURI) {
                return null;
            }

            @Override
            public Iterator<String> getPrefixes(String namespaceURI) {
                return null;
            }
        });

        // Probar diferentes variantes de XPath
        String[] expressions = expression.split("\\|");
        for (String expr : expressions) {
            String[] xpathVariants = {
                    "//*[local-name()='" + expr + "']",
                    "//ns1:" + expr,
                    "//ns2:" + expr,
                    "//factura:" + expr
            };

            for (String xpathExpr : xpathVariants) {
                try {
                    System.out.println("Probando XPath: " + xpathExpr); // 👈 Log de depuración
                    String value = xpath.compile(xpathExpr).evaluate(doc);
                    if (value != null && !value.trim().isEmpty()) {
                        System.out.println("Encontrado valor: " + value); // 👈 Valor encontrado
                        return value.trim();
                    }
                } catch (XPathExpressionException e) {
                    System.err.println("Error en XPath: " + xpathExpr + " - " + e.getMessage());
                }
            }
        }
        return null;
    } catch (Exception e) {
        return null;
    }
}

}
