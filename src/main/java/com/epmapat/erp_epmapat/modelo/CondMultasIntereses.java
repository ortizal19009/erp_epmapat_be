package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

@Entity
@Table(name = "condmultasintereses")
public class CondMultasIntereses {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idcondmultainteres;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfactura_facturas")
    private Facturas idfactura_facturas;
    private BigDecimal totalinteres;
    private BigDecimal totalmultas;
    private String razoncondonacion;
    private Long usucrea;
    private LocalDateTime feccrea;
    private String estado;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idusaprueba")
    private Usuarios usuarioAprueba;
    private LocalDateTime fecaprobacion;
    private String observacion_aprobacion;
    public Long getIdcondmultainteres() {
        return idcondmultainteres;
    }
    public void setIdcondmultainteres(Long idcondmultainteres) {
        this.idcondmultainteres = idcondmultainteres;
    }
    public Facturas getIdfactura_facturas() {
        return idfactura_facturas;
    }
    public void setIdfactura_facturas(Facturas idfactura_facturas) {
        this.idfactura_facturas = idfactura_facturas;
    }
    public BigDecimal getTotalinteres() {
        return totalinteres;
    }
    public void setTotalinteres(BigDecimal totalinteres) {
        this.totalinteres = totalinteres;
    }
    public BigDecimal getTotalmultas() {
        return totalmultas;
    }
    public void setTotalmultas(BigDecimal totalmultas) {
        this.totalmultas = totalmultas;
    }
    public String getRazoncondonacion() {
        return razoncondonacion;
    }
    public void setRazoncondonacion(String razoncondonacion) {
        this.razoncondonacion = razoncondonacion;
    }
    public Long getUsucrea() {
        return usucrea;
    }
    public void setUsucrea(Long usucrea) {
        this.usucrea = usucrea;
    }
    public LocalDateTime getFeccrea() {
        return feccrea;
    }
    public void setFeccrea(LocalDateTime feccrea) {
        this.feccrea = feccrea;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public Usuarios getUsuarioAprueba() {
        return usuarioAprueba;
    }
    public void setUsuarioAprueba(Usuarios usuarioAprueba) {
        this.usuarioAprueba = usuarioAprueba;
    }
    public LocalDateTime getFecaprobacion() {
        return fecaprobacion;
    }
    public void setFecaprobacion(LocalDateTime fecaprobacion) {
        this.fecaprobacion = fecaprobacion;
    }
    public String getObservacion_aprobacion() {
        return observacion_aprobacion;
    }
    public void setObservacion_aprobacion(String observacion_aprobacion) {
        this.observacion_aprobacion = observacion_aprobacion;
    }


}
