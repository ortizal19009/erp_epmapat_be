package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
//import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Estadom implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idestadom;
    String descripcion;
    Long usucrea;
    //Date feccrea;
    //Long usumodi;
    //Date fecmodi;

    public Estadom() {
    }

    public Estadom(Long idestadom, String descripcion, Long usucrea) {
        this.idestadom = idestadom;
        this.descripcion = descripcion;
        this.usucrea = usucrea;
      //  this.feccrea = feccrea;
      //  this.usumodi = usumodi;
      //  this.fecmodi = fecmodi;
    }

    public Long getIdestadom() {
        return idestadom;
    }

    public void setIdestadom(Long idestadom) {
        this.idestadom = idestadom;
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
}
