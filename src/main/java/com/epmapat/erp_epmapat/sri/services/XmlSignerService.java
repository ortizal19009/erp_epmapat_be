package com.epmapat.erp_epmapat.sri.services;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.SignatureMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.util.Collections;
import java.util.List;

@Service
public class XmlSignerService {

    public String signXml(String xmlContent, byte[] certificado, String password) throws Exception {
        if (certificado == null || certificado.length == 0) {
            throw new IllegalArgumentException("El certificado proporcionado está vacío o es nulo");
        }

        if (xmlContent == null || xmlContent.trim().isEmpty()) {
            throw new IllegalArgumentException("El contenido XML es nulo o vacío");
        }

        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("La contraseña del certificado es nula o vacía");
        }

        // Cargar el certificado desde el almacén
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(certificado), password.toCharArray());

        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        X509Certificate cert = (X509Certificate) keyStore.getCertificate(alias);

        /*
         * // Parsear el XML
         * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         * dbf.setNamespaceAware(true);
         * DocumentBuilder db = dbf.newDocumentBuilder();
         * // Document document = db.parse(new
         * ByteArrayInputStream(xmlContent.getBytes()));
         * Document document = db.parse(new
         * ByteArrayInputStream(xmlContent.getBytes(StandardCharsets.UTF_8)));
         */

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(new InputSource(new StringReader(xmlContent))); // <- ahora está definido

        // Crear XML Signature Factory
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Referencia al documento XML
        Reference reference = signatureFactory
                .newReference("", signatureFactory.newDigestMethod(DigestMethod.SHA256, null),
                        Collections.singletonList(
                                signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null, null);

        // Crear SignedInfo
        SignedInfo signedInfo = signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null),
                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, null),
                Collections.singletonList(reference));

        // Crear KeyInfo con datos del certificado
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        List<X509Certificate> x509Content = Collections.singletonList(cert);
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

        // Firmar el documento
        DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
        XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(domSignContext);

        // Convertir el documento firmado a String
        StringWriter stringWriter = new StringWriter();
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(new DOMSource(document), new StreamResult(stringWriter));

        return stringWriter.toString();
    }

    public byte[] _signXml(Document xmlData, byte[] p12Data, String password) throws Exception {

        // Cargar el KeyStore desde los bytes del certificado
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        keyStore.load(new ByteArrayInputStream(p12Data), password.toCharArray());

        // Obtener la clave privada y el certificado
        String alias = keyStore.aliases().nextElement();
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, password.toCharArray());
        X509Certificate certificate = (X509Certificate) keyStore.getCertificate(alias);

        // Preparar el XML para firmar
        /*
         * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
         * dbf.setNamespaceAware(true);
         * DocumentBuilder builder = dbf.newDocumentBuilder();
         * Document document = builder.parse(new ByteArrayInputStream(xmlData));
         */
        Document document = xmlData;

        // Crear XML Signature Factory
        XMLSignatureFactory xmlSigFactory = XMLSignatureFactory.getInstance("DOM");

        // Crear referencia a todo el documento
        Reference reference = xmlSigFactory.newReference("", xmlSigFactory.newDigestMethod(DigestMethod.SHA256, null));

        // Crear SignedInfo
        SignedInfo signedInfo = xmlSigFactory.newSignedInfo(
                xmlSigFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE,
                        (C14NMethodParameterSpec) null),
                xmlSigFactory.newSignatureMethod(SignatureMethod.RSA_SHA256, (SignatureMethodParameterSpec) null),
                Collections.singletonList(reference));

        // Crear KeyInfo
        KeyInfoFactory keyInfoFactory = xmlSigFactory.getKeyInfoFactory();
        X509Data x509Data = keyInfoFactory.newX509Data(Collections.singletonList(certificate));
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

        // Firmar el documento
        DOMSignContext domSignContext = new DOMSignContext(privateKey, document.getDocumentElement());
        XMLSignature signature = xmlSigFactory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(domSignContext);

        // Convertir el documento firmado a bytes
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        javax.xml.transform.TransformerFactory.newInstance().newTransformer()
                .transform(new javax.xml.transform.dom.DOMSource(document),
                        new javax.xml.transform.stream.StreamResult(outputStream));

        return outputStream.toByteArray();
    }
}