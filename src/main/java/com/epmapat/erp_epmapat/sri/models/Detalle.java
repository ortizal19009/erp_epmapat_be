package com.epmapat.erp_epmapat.sri.models;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.xml.bind.annotation.*;
import java.math.BigDecimal;
import java.util.List;

@Data
@Getter
@Setter
@XmlAccessorType(XmlAccessType.FIELD)
public class Detalle {

    @XmlElement(name = "codigoPrincipal", required = true)
    private String codigoPrincipal;

    @XmlElement(name = "codigoAuxiliar")
    private String codigoAuxiliar;

    @XmlElement(name = "descripcion", required = true)
    private String descripcion;

    @XmlElement(name = "cantidad", required = true)
    private BigDecimal cantidad;

    @XmlElement(name = "precioUnitario", required = true)
    private BigDecimal precioUnitario;

    @XmlElement(name = "descuento", required = true)
    private BigDecimal descuento;

    @XmlElement(name = "precioTotalSinImpuesto", required = true)
    private BigDecimal precioTotalSinImpuesto;

    @XmlElementWrapper(name = "detallesAdicionales")
    @XmlElement(name = "detAdicional")
    private List<DetalleAdicional> detallesAdicionales;

    @XmlElementWrapper(name = "impuestos", required = true)
    @XmlElement(name = "impuesto")
    private List<Impuesto> impuestos;

    // Clase para detalles adicionales (opcional)
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class DetalleAdicional {
        @XmlAttribute
        private String nombre;
        
        @XmlValue
        private String valor;
    }

    // Clase para impuestos
    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Impuesto {
        @XmlElement(name = "codigo", required = true)
        private String codigo; // "2" IVA, "3" ICE, etc.
        
        @XmlElement(name = "codigoPorcentaje", required = true)
        private String codigoPorcentaje; // "0" 0%, "2" 12%, etc.
        
        @XmlElement(name = "tarifa")
        private BigDecimal tarifa; // Solo para algunos impuestos
        
        @XmlElement(name = "baseImponible", required = true)
        private BigDecimal baseImponible;
        
        @XmlElement(name = "valor", required = true)
        private BigDecimal valor;
    }
}