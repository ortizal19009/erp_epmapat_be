package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tpreclamo")
public class Tpreclamo implements Serializable{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtpreclamo;
    String descripcion;
    Long usucrea;
    Date feccrea;
    Long usumodi;
    Date fecmodi;
    
    public Tpreclamo() {
    }

    public Tpreclamo(Long idtpreclamo, String descripcion, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
        this.idtpreclamo = idtpreclamo;
        this.descripcion = descripcion;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtpreclamo() {
        return idtpreclamo;
    }

    public void setIdtpreclamo(Long idtpreclamo) {
        this.idtpreclamo = idtpreclamo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
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

}
