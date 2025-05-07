package com.epmapat.erp_epmapat.controlador.sri.configuratios;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.*;
@Data
@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "sri")
public class SRIProperties {
    private Integer ambiente;
    private Integer tipoEmision;
    private String ruc;
    private String razonSocial;
    private String nombreComercial;
    private String dirMatriz;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String xsdPath;
    private String keystorePath;
    private String keystorePassword;
    private String keyAlias;
    private String keyPassword;
    private String wsRecepcionUrl;
    private String wsAutorizacionUrl;

   

    // Continuar con los demás getters y setters...
    // (Puedes usar Lombok @Data si lo prefieres)
}