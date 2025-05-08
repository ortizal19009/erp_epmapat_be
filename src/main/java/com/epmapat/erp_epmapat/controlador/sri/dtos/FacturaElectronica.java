package com.epmapat.erp_epmapat.controlador.sri.dtos;

import java.math.BigDecimal;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.*;
@Data
@Getter
@Setter
@XmlRootElement(name = "factura")
@XmlAccessorType(XmlAccessType.FIELD)
public class FacturaElectronica {
    @XmlAttribute
    private String id;
    @XmlAttribute
    private String version = "1.1.0";
    
    private InfoTributaria infoTributaria;
    private InfoFactura infoFactura;
    private List<Detalle> detalles;
    private List<Pago> pagos;
    private BigDecimal totalImpuestos;
    private BigDecimal importeTotal;
    
    // Getters y Setters
}