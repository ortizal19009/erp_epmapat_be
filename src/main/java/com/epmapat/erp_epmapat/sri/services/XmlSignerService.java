package com.epmapat.erp_epmapat.sri.services;

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.crypto.dsig.*;
import javax.xml.crypto.dsig.dom.DOMSignContext;
import javax.xml.crypto.dsig.keyinfo.KeyInfo;
import javax.xml.crypto.dsig.keyinfo.KeyInfoFactory;
import javax.xml.crypto.dsig.keyinfo.X509Data;
import javax.xml.crypto.dsig.spec.C14NMethodParameterSpec;
import javax.xml.crypto.dsig.spec.TransformParameterSpec;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collections;
import java.util.List;
@Service
public class XmlSignerService {

    public Document signXml(File xmlFile, File p12File, String p12Password, String alias) throws Exception {
        // Cargar KeyStore
        KeyStore keyStore = KeyStore.getInstance("PKCS12");
        try (FileInputStream fis = new FileInputStream(p12File)) {
            keyStore.load(fis, p12Password.toCharArray());
        }

        // Obtener la clave privada y el certificado
        PrivateKey privateKey = (PrivateKey) keyStore.getKey(alias, p12Password.toCharArray());
        Certificate cert = keyStore.getCertificate(alias);

        // Preparar el documento XML
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(true);
        DocumentBuilder builder = dbf.newDocumentBuilder();
        Document doc = builder.parse(xmlFile);

        // Crear el XML Signature Factory
        XMLSignatureFactory signatureFactory = XMLSignatureFactory.getInstance("DOM");

        // Definir el método de firma y la canonicalización
        SignedInfo signedInfo = signatureFactory.newSignedInfo(
                signatureFactory.newCanonicalizationMethod(CanonicalizationMethod.INCLUSIVE, (C14NMethodParameterSpec) null),
                signatureFactory.newSignatureMethod(SignatureMethod.RSA_SHA1, null),
                Collections.singletonList(signatureFactory.newReference(
                        "",
                        signatureFactory.newDigestMethod(DigestMethod.SHA1, null),
                        Collections.singletonList(signatureFactory.newTransform(Transform.ENVELOPED, (TransformParameterSpec) null)),
                        null,
                        null
                ))
        );

        // Configuración del KeyInfo
        KeyInfoFactory keyInfoFactory = signatureFactory.getKeyInfoFactory();
        List<Object> x509Content = Collections.singletonList(cert);
        X509Data x509Data = keyInfoFactory.newX509Data(x509Content);
        KeyInfo keyInfo = keyInfoFactory.newKeyInfo(Collections.singletonList(x509Data));

        // Firmar el XML
        DOMSignContext signContext = new DOMSignContext(privateKey, doc.getDocumentElement());
        XMLSignature signature = signatureFactory.newXMLSignature(signedInfo, keyInfo);
        signature.sign(signContext);

        return doc;
    }
}
