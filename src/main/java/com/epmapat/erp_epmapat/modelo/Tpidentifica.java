package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "tpidentifica")

public class Tpidentifica implements Serializable {

    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtpidentifica;
    String codigo;
    String nombre;
    Long usucrea;
    Date feccrea;
    Long usumodi;
    Date fecmodi;
    
    public Tpidentifica() {
    }

    public Tpidentifica(Long idtpidentifica, String codigo, String nombre, Long usucrea, Date feccrea, Long usumodi, Date fecmodi ) {
        this.idtpidentifica = idtpidentifica;
        this.codigo = codigo;
        this.nombre = nombre;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtpidentifica() {
        return idtpidentifica;
    }

    public void setIdtpidentifica(Long idtpidentifica) {
        this.idtpidentifica = idtpidentifica;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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
