package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name= "fec_factura_detalles")
public class Fec_factura_detalles {
    @Id
    private Long idfacturadetalle; 
    private Long idfactura; 
    private String codigoprincipal; 
    private String descripcion; 
    private BigDecimal cantidad; 
    private BigDecimal preciounitario; 
    private BigDecimal descuento;
    public Long getIdfacturadetalle() {
        return idfacturadetalle;
    }
    public void setIdfacturadetalle(Long idfacturadetalle) {
        this.idfacturadetalle = idfacturadetalle;
    }
    public Long getIdfactura() {
        return idfactura;
    }
    public void setIdfactura(Long idfactura) {
        this.idfactura = idfactura;
    }
    public String getCodigoprincipal() {
        return codigoprincipal;
    }
    public void setCodigoprincipal(String codigoprincipal) {
        this.codigoprincipal = codigoprincipal;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
    public BigDecimal getCantidad() {
        return cantidad;
    }
    public void setCantidad(BigDecimal cantidad) {
        this.cantidad = cantidad;
    }
    public BigDecimal getPreciounitario() {
        return preciounitario;
    }
    public void setPreciounitario(BigDecimal preciounitario) {
        this.preciounitario = preciounitario;
    }
    public BigDecimal getDescuento() {
        return descuento;
    }
    public void setDescuento(BigDecimal descuento) {
        this.descuento = descuento;
    }
/*     public Fec_factura_detalles(Long idfacturadetalle, Long idfactura, String codigoprincipal, String descripcion,
            BigDecimal cantidad, BigDecimal preciounitario, BigDecimal descuento) {
        this.idfacturadetalle = idfacturadetalle;
        this.idfactura = idfactura;
        this.codigoprincipal = codigoprincipal;
        this.descripcion = descripcion;
        this.cantidad = cantidad;
        this.preciounitario = preciounitario;
        this.descuento = descuento;
    }  */
    public Fec_factura_detalles(){
        super();
    }
    
}
