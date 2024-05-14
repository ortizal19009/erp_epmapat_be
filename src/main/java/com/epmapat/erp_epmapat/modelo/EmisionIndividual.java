package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "emisionindividual")
public class EmisionIndividual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemisionindividual;
    private Long idemision;
    private Long idlecturanueva;
    private Long idlecturaanterior;

    public Long getIdemisionindividual() {
        return idemisionindividual;
    }

    public void setIdemisionindividual(Long idemisionindividual) {
        this.idemisionindividual = idemisionindividual;
    }

    public Long getIdemision() {
        return idemision;
    }

    public void setIdemision(Long idemision) {
        this.idemision = idemision;
    }

    public Long getIdlecturanueva() {
        return idlecturanueva;
    }

    public void setIdlecturanueva(Long idlecturanueva) {
        this.idlecturanueva = idlecturanueva;
    }

    public Long getIdlecturaanterior() {
        return idlecturaanterior;
    }

    public void setIdlecturaanterior(Long idlecturaanterior) {
        this.idlecturaanterior = idlecturaanterior;
    }

    public EmisionIndividual() {
        super();
    }

    public EmisionIndividual(Long idemisionindividual, Long idemision, Long idlecturanueva, Long idlecturaanterior) {
        this.idemisionindividual = idemisionindividual;
        this.idemision = idemision;
        this.idlecturanueva = idlecturanueva;
        this.idlecturaanterior = idlecturaanterior;
    }
    
}
