package com.epmapat.erp_epmapat.sri.services;

import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

/** Reads authorization metadata before extracting its invoice. */
final class FacturaPdfXml {
    final Document factura;
    final String numeroAutorizacion;
    final String fechaAutorizacion;
    private FacturaPdfXml(Document factura, String numero, String fecha) {
        this.factura = factura; this.numeroAutorizacion = numero; this.fechaAutorizacion = fecha;
    }
    static String text(Document doc, String tag) {
        NodeList nodes = doc.getElementsByTagNameNS("*", tag);
        return nodes.getLength() == 0 ? "" : nodes.item(0).getTextContent().trim();
    }
    static FacturaPdfXml parse(String xml) throws Exception {
        Document outer = document(xml);
        Element root = outer.getDocumentElement();
        String numero = "", fecha = "";
        if (!"factura".equals(root.getLocalName())) {
            NodeList autorizaciones = outer.getElementsByTagNameNS("*", "autorizacion");
            if (autorizaciones.getLength() > 1) throw new IllegalArgumentException("El XML contiene varias autorizaciones; seleccione una factura");
            numero = text(outer, "numeroAutorizacion");
            fecha = text(outer, "fechaAutorizacion");
            NodeList comprobantes = outer.getElementsByTagNameNS("*", "comprobante");
            if (comprobantes.getLength() != 1) throw new IllegalArgumentException("No se encontro un comprobante unico en el XML");
            Element comprobante = (Element) comprobantes.item(0);
            Element embedded = null;
            for (Node n = comprobante.getFirstChild(); n != null; n = n.getNextSibling()) {
                if (n instanceof Element) { embedded = (Element) n; break; }
            }
            if (embedded != null) {
                Document inner = factory().newDocumentBuilder().newDocument();
                inner.appendChild(inner.importNode(embedded, true));
                outer = inner;
            } else {
                outer = document(comprobante.getTextContent());
            }
        }
        if (!"factura".equals(outer.getDocumentElement().getLocalName())) {
            throw new IllegalArgumentException("El comprobante no es una factura");
        }
        String clave = text(outer, "claveAcceso");
        if (!clave.matches("[0-9]{49}")) throw new IllegalArgumentException("La factura no contiene una clave de acceso de 49 digitos");
        return new FacturaPdfXml(outer, numero, fecha);
    }
    private static DocumentBuilderFactory factory() throws Exception {
        DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
        f.setNamespaceAware(true);
        f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        f.setFeature("http://xml.org/sax/features/external-general-entities", false);
        f.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        f.setXIncludeAware(false);
        return f;
    }
    private static Document document(String xml) throws Exception {
        if (xml == null || xml.isBlank()) throw new IllegalArgumentException("XML vacio");
        String value = xml.replace("\uFEFF", "").trim();
        if (value.startsWith("&lt;")) value = value.replace("&lt;", "<").replace("&gt;", ">").replace("&quot;", "\"").replace("&apos;", "'").replace("&amp;", "&");
        return factory().newDocumentBuilder().parse(new InputSource(new StringReader(value)));
    }
}
