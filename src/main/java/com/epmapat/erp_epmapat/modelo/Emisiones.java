package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emisiones")

public class Emisiones implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemision;
    String emision;
    Integer estado;
    String observaciones;
    Long usuariocierre;
    @Column(name = "fechacierre")
    private ZonedDateTime fechacierre;
    Long m3;
    Long usucrea;
    Date feccrea;
    Long usumodi;
    Date fecmodi;

    /* CONSTRUCTORES */
    // public Emisiones() {
    // }
    // public Emisiones(String emision) {
    // this.emision = emision;
    // }

    /* ================== GETTERS Y SETTERS ========== */
    public Long getIdemision() {
        return idemision;
    }

    public Long getUsuariocierre() {
        return usuariocierre;
    }

    public void setUsuariocierre(Long usuariocierre) {
        this.usuariocierre = usuariocierre;
    }

    public ZonedDateTime getFechacierre() {
        return fechacierre;
    }

    public void setFechacierre(ZonedDateTime fechacierre) {
        this.fechacierre = fechacierre;
    }

    public void setIdemision(Long idemision) {
        this.idemision = idemision;
    }

    public String getEmision() {
        return emision;
    }

    public void setEmision(String emision) {
        this.emision = emision;
    }

    public Integer getEstado() {
        return estado;
    }

    public void setEstado(Integer estado) {
        this.estado = estado;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public Long getUsucrea() {
        return usucrea;
    }

    public void setUsucrea(Long usucrea) {
        this.usucrea = usucrea;
    }

    public Date getFeccrea() {
        return feccrea;
    }

    public void setFeccrea(Date feccrea) {
        this.feccrea = feccrea;
    }

    public Long getUsumodi() {
        return usumodi;
    }

    public void setUsumodi(Long usumodi) {
        this.usumodi = usumodi;
    }

    public Date getFecmodi() {
        return fecmodi;
    }

    public void setFecmodi(Date fecmodi) {
        this.fecmodi = fecmodi;
    }

    public Long getM3() {
        return m3;
    }

    public void setM3(Long m3) {
        this.m3 = m3;
    }

}
