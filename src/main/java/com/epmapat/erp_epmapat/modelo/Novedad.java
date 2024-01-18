package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Table(name = "novedades")

public class Novedad implements Serializable{

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idnovedad;
    private String descripcion;
    Integer estado;
    Long usucrea;
    //Date feccrea;
    
    public Novedad() {
    }

    public Novedad(Long idnovedad, String descripcion, Integer estado, long usucrea, Date feccrea) {
        this.descripcion = descripcion;
        this.estado = estado;
        this.usucrea = usucrea;
        //this.feccrea = feccrea;
    }
    public Long getIdnovedad() {
        return idnovedad;
    }
    public void setIdnovedad(Long idnovedad) {
        this.idnovedad = idnovedad;
    }
    public String getDescripcion() {
        return descripcion;
    }
    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
     }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Long getUsucrea() {
        return usucrea;
    }
    public void setUsucrea(Long usucrea) {
        this.usucrea = usucrea;
    }
    
    @Override
    public String toString() {
        return "Novedad [descripcion=" + descripcion + ", estado=" + estado + ", idnovedad=" + idnovedad + ", usucrea="
                + usucrea + "]";
    }

/*     public Date getFeccrea() {
        return feccrea;
    }

    public void setFeccrea(Date feccrea) {
        this.feccrea = feccrea;
    }*/

}
