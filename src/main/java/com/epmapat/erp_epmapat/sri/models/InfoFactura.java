package com.epmapat.erp_epmapat.sri.models;

import lombok.Data;
import javax.xml.bind.annotation.*;
import java.math.BigDecimal;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
public class InfoFactura {

    @XmlElement(name = "fechaEmision", required = true)
    private String fechaEmision;
    @XmlElement(name = "obligadoContabilidad")
    private String obligadoContabilidad; // "SI" o "NO"
    @XmlElement(name = "tipoIdentificacionComprador", required = true)
    private String tipoIdentificacionComprador; // "04" RUC, "05" Cédula, "06" Pasaporte, etc.
    @XmlElement(name = "razonSocialComprador", required = true)
    private String razonSocialComprador;
    
    @XmlElement(name = "identificacionComprador", required = true)
    private String identificacionComprador;


    @XmlElement(name = "direccionComprador")
    private String direccionComprador;
    @XmlElement(name = "contribuyenteEspecial")
    private String contribuyenteEspecial;
    @XmlElement(name = "totalSinImpuestos", required = true)
    private BigDecimal totalSinImpuestos;

    @XmlElement(name = "totalDescuento", required = true)
    private BigDecimal totalDescuento;
    @XmlElement(name = "totalConImpuestos")
    private TotalConImpuestos totalConImpuestos;

    @XmlElement(name = "propina", required = true)
    private BigDecimal propina = BigDecimal.ZERO;

    @XmlElement(name = "importeTotal", required = true)
    private BigDecimal importeTotal;

    @XmlElement(name = "moneda", required = true)
    private String moneda = "DOLAR";

    /*
     * @XmlElement(name = "pagos")
     * private Pagos pagos; // Opcional para facturas con pagos
     */

    @Data
    @XmlAccessorType(XmlAccessType.FIELD)
    public static class Pago {
        @XmlElement(name = "formaPago", required = true)
        private String formaPago; // "01" Efectivo, "15" Tarjeta crédito, etc.

        @XmlElement(name = "total", required = true)
        private BigDecimal total;

        @XmlElement(name = "plazo")
        private String plazo; // Opcional

        @XmlElement(name = "unidadTiempo")
        private String unidadTiempo; // Opcional (dias, meses)
    }
}
