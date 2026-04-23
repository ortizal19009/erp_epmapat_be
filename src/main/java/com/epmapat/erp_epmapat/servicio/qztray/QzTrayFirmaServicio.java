package com.epmapat.erp_epmapat.servicio.qztray;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.util.Base64;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

@Service
public class QzTrayFirmaServicio {

    private static final String KEYSTORE_RESOURCE = "qztray/qztray.p12";

    @Value("${qztray.keystore-password:epmapat-qztray}")
    private String keystorePassword;

    private X509Certificate certificate;
    private PrivateKey privateKey;

    @PostConstruct
    public void init() {
        try {
            ClassPathResource resource = new ClassPathResource(KEYSTORE_RESOURCE);
            if (!resource.exists()) {
                throw new IllegalStateException("No existe el keystore de QZ Tray en " + KEYSTORE_RESOURCE);
            }

            KeyStore keystore = KeyStore.getInstance("PKCS12");
            try (InputStream in = resource.getInputStream()) {
                keystore.load(in, keystorePassword.toCharArray());
            }

            String alias = keystore.aliases().nextElement();
            this.privateKey = (PrivateKey) keystore.getKey(alias, keystorePassword.toCharArray());
            this.certificate = (X509Certificate) keystore.getCertificate(alias);

            if (this.privateKey == null || this.certificate == null) {
                throw new IllegalStateException("No se pudo cargar la clave o el certificado de QZ Tray.");
            }
        } catch (Exception e) {
            throw new IllegalStateException("Error inicializando la firma para QZ Tray.", e);
        }
    }

    public String getCertificatePem() {
        try {
            return toPem("CERTIFICATE", certificate.getEncoded());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo generar el certificado PEM.", e);
        }
    }

    public String sign(String request) {
        if (request == null) {
            throw new IllegalArgumentException("La petición a firmar no puede ser nula.");
        }

        try {
            Signature signature = Signature.getInstance("SHA512withRSA");
            signature.initSign(privateKey);
            signature.update(request.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(signature.sign());
        } catch (Exception e) {
            throw new IllegalStateException("No se pudo firmar la petición para QZ Tray.", e);
        }
    }

    private String toPem(String type, byte[] content) {
        String base64 = Base64.getMimeEncoder(64, "\n".getBytes(StandardCharsets.US_ASCII))
                .encodeToString(content);
        return "-----BEGIN " + type + "-----\n" + base64 + "\n-----END " + type + "-----\n";
    }
}
