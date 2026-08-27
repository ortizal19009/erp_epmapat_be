package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CondonacionResponse {
    private Long idcondmultainteres;
    private Long idfactura;
    private String nrofactura;
    private Long idcliente;
    private String abonado;
    private Long cuenta;
    private BigDecimal totalinteres;
    private BigDecimal totalmultas;
    @JsonAlias("razoncondonacion")
    private String razonExoneracion;
    private String estado;
    private Long usucrea;
    private String usuarioCreador;
    private LocalDateTime feccrea;
    private Long idusaprueba;
    private String usuarioAprueba;
    private LocalDateTime fecaprobacion;
    private String observacionAprobacion;
    private LocalDate fechaFactura;

    public Long getIdcondmultainteres() { return idcondmultainteres; }
    public void setIdcondmultainteres(Long idcondmultainteres) { this.idcondmultainteres = idcondmultainteres; }
    public Long getIdfactura() { return idfactura; }
    public void setIdfactura(Long idfactura) { this.idfactura = idfactura; }
    public String getNrofactura() { return nrofactura; }
    public void setNrofactura(String nrofactura) { this.nrofactura = nrofactura; }
    public Long getIdcliente() { return idcliente; }
    public void setIdcliente(Long idcliente) { this.idcliente = idcliente; }
    public String getAbonado() { return abonado; }
    public void setAbonado(String abonado) { this.abonado = abonado; }
    public Long getCuenta() { return cuenta; }
    public void setCuenta(Long cuenta) { this.cuenta = cuenta; }
    public BigDecimal getTotalinteres() { return totalinteres; }
    public void setTotalinteres(BigDecimal totalinteres) { this.totalinteres = totalinteres; }
    public BigDecimal getTotalmultas() { return totalmultas; }
    public void setTotalmultas(BigDecimal totalmultas) { this.totalmultas = totalmultas; }
    public String getRazonExoneracion() { return razonExoneracion; }
    public void setRazonExoneracion(String razonExoneracion) { this.razonExoneracion = razonExoneracion; }
    public String getRazoncondonacion() { return razonExoneracion; }
    public void setRazoncondonacion(String razoncondonacion) { this.razonExoneracion = razoncondonacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Long getUsucrea() { return usucrea; }
    public void setUsucrea(Long usucrea) { this.usucrea = usucrea; }
    public String getUsuarioCreador() { return usuarioCreador; }
    public void setUsuarioCreador(String usuarioCreador) { this.usuarioCreador = usuarioCreador; }
    public LocalDateTime getFeccrea() { return feccrea; }
    public void setFeccrea(LocalDateTime feccrea) { this.feccrea = feccrea; }
    public Long getIdusaprueba() { return idusaprueba; }
    public void setIdusaprueba(Long idusaprueba) { this.idusaprueba = idusaprueba; }
    public String getUsuarioAprueba() { return usuarioAprueba; }
    public void setUsuarioAprueba(String usuarioAprueba) { this.usuarioAprueba = usuarioAprueba; }
    public LocalDateTime getFecaprobacion() { return fecaprobacion; }
    public void setFecaprobacion(LocalDateTime fecaprobacion) { this.fecaprobacion = fecaprobacion; }
    public String getObservacionAprobacion() { return observacionAprobacion; }
    public void setObservacionAprobacion(String observacionAprobacion) { this.observacionAprobacion = observacionAprobacion; }
    public LocalDate getFechaFactura() { return fechaFactura; }
    public void setFechaFactura(LocalDate fechaFactura) { this.fechaFactura = fechaFactura; }
}
