package com.epmapat.erp_epmapat.sri.services;

import java.io.ByteArrayInputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@Service
public class RetencionValidator {

   public void validarXmlAntesDeFirmar(String xml) {
      List<String> errores = new ArrayList<>();
      Document document = parse(xml, errores);
      if (document != null) {
         validarRaiz(document, errores);
         validarCamposObligatorios(document, errores);
         validarImpuestos(document, errores);
      }
      if (!errores.isEmpty()) {
         throw new IllegalStateException("XML de retencion invalido: " + String.join(" | ", errores));
      }
   }

   private Document parse(String xml, List<String> errores) {
      if (xml == null || xml.isBlank()) {
         errores.add("El XML no puede estar vacio.");
         return null;
      }
      try {
         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
         factory.setNamespaceAware(true);
         factory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
         return factory.newDocumentBuilder()
               .parse(new ByteArrayInputStream(xml.getBytes(StandardCharsets.UTF_8)));
      } catch (Exception ex) {
         errores.add("El XML no esta bien formado: " + ex.getMessage());
         return null;
      }
   }

   private void validarRaiz(Document document, List<String> errores) {
      Element root = document.getDocumentElement();
      if (root == null || !"comprobanteRetencion".equals(root.getTagName())) {
         errores.add("El nodo raiz debe ser comprobanteRetencion.");
         return;
      }
      if (!"comprobante".equals(root.getAttribute("id"))) {
         errores.add("El atributo id del comprobante debe ser comprobante.");
      }
      if (!"1.0.0".equals(root.getAttribute("version"))) {
         errores.add("La version del comprobante debe ser 1.0.0.");
      }
      if (root.getAttribute("xmlns:xsi") == null || root.getAttribute("xmlns:xsi").isBlank()) {
         errores.add("Falta xmlns:xsi.");
      }
      if (root.getAttribute("xsi:noNamespaceSchemaLocation") == null
            || root.getAttribute("xsi:noNamespaceSchemaLocation").isBlank()) {
         errores.add("Falta xsi:noNamespaceSchemaLocation.");
      }
   }

   private void validarCamposObligatorios(Document document, List<String> errores) {
      validarTexto(document, "ambiente", "[12]", "ambiente debe ser 1 o 2.", errores);
      validarTexto(document, "tipoEmision", "1", "tipoEmision debe ser 1.", errores);
      validarTexto(document, "codDoc", "07", "codDoc debe ser 07.", errores);
      validarTexto(document, "ruc", "\\d{13}", "ruc debe tener 13 digitos.", errores);
      validarTexto(document, "claveAcceso", "\\d{49}", "claveAcceso debe tener 49 digitos.", errores);
      validarTexto(document, "estab", "\\d{3}", "estab debe tener 3 digitos.", errores);
      validarTexto(document, "ptoEmi", "\\d{3}", "ptoEmi debe tener 3 digitos.", errores);
      validarTexto(document, "secuencial", "\\d{9}", "secuencial debe tener 9 digitos.", errores);
      validarNoVacio(document, "fechaEmision", errores);
      validarNoVacio(document, "razonSocialSujetoRetenido", errores);
      validarTexto(document, "identificacionSujetoRetenido", "\\d{10}|\\d{13}",
            "identificacionSujetoRetenido debe tener 10 o 13 digitos.", errores);
      validarTexto(document, "periodoFiscal", "\\d{2}/\\d{4}", "periodoFiscal debe tener formato MM/yyyy.", errores);
   }

   private void validarImpuestos(Document document, List<String> errores) {
      NodeList impuestos = document.getElementsByTagName("impuesto");
      if (impuestos == null || impuestos.getLength() == 0) {
         errores.add("Debe existir al menos un impuesto de retencion.");
         return;
      }
      for (int i = 0; i < impuestos.getLength(); i++) {
         Element impuesto = (Element) impuestos.item(i);
         BigDecimal base = decimal(impuesto, "baseImponible", errores, i);
         BigDecimal porcentaje = decimal(impuesto, "porcentajeRetener", errores, i);
         BigDecimal valor = decimal(impuesto, "valorRetenido", errores, i);
         if (base.compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El impuesto " + (i + 1) + " debe tener baseImponible mayor a cero.");
         }
         if (porcentaje.compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El impuesto " + (i + 1) + " debe tener porcentajeRetener mayor a cero.");
         }
         if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            errores.add("El impuesto " + (i + 1) + " debe tener valorRetenido mayor a cero.");
         }
      }
   }

   private BigDecimal decimal(Element parent, String tag, List<String> errores, int index) {
      String value = texto(parent, tag);
      try {
         return value == null || value.isBlank() ? BigDecimal.ZERO : new BigDecimal(value);
      } catch (NumberFormatException ex) {
         errores.add("El impuesto " + (index + 1) + " tiene " + tag + " no numerico.");
         return BigDecimal.ZERO;
      }
   }

   private void validarTexto(Document document, String tag, String regex, String mensaje, List<String> errores) {
      String value = texto(document, tag);
      if (value == null || !value.matches(regex)) {
         errores.add(mensaje);
      }
   }

   private void validarNoVacio(Document document, String tag, List<String> errores) {
      String value = texto(document, tag);
      if (value == null || value.isBlank()) {
         errores.add(tag + " es obligatorio.");
      }
   }

   private String texto(Document document, String tag) {
      NodeList nodes = document.getElementsByTagName(tag);
      if (nodes == null || nodes.getLength() == 0) {
         return null;
      }
      return nodes.item(0).getTextContent() == null ? "" : nodes.item(0).getTextContent().trim();
   }

   private String texto(Element parent, String tag) {
      NodeList nodes = parent.getElementsByTagName(tag);
      if (nodes == null || nodes.getLength() == 0) {
         return null;
      }
      return nodes.item(0).getTextContent() == null ? "" : nodes.item(0).getTextContent().trim();
   }
}
