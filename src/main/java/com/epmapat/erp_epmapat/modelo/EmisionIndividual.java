package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "emisionindividual")
public class EmisionIndividual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idemisionindividual;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idemision")
    private Emisiones idemision;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlecturanueva")
    private Lecturas idlecturanueva;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idlecturaanterior")
    private Lecturas idlecturaanterior;

    public EmisionIndividual() {
        super();
    }

    public Long getIdemisionindividual() {
        return idemisionindividual;
    }

    public void setIdemisionindividual(Long idemisionindividual) {
        this.idemisionindividual = idemisionindividual;
    }

    public Emisiones getIdemision() {
        return idemision;
    }

    public void setIdemision(Emisiones idemision) {
        this.idemision = idemision;
    }

    public Lecturas getIdlecturanueva() {
        return idlecturanueva;
    }

    public void setIdlecturanueva(Lecturas idlecturanueva) {
        this.idlecturanueva = idlecturanueva;
    }

    public Lecturas getIdlecturaanterior() {
        return idlecturaanterior;
    }

    public void setIdlecturaanterior(Lecturas idlecturaanterior) {
        this.idlecturaanterior = idlecturaanterior;
    }

    public EmisionIndividual(Long idemisionindividual, Emisiones idemision, Lecturas idlecturanueva,
            Lecturas idlecturaanterior) {
        this.idemisionindividual = idemisionindividual;
        this.idemision = idemision;
        this.idlecturanueva = idlecturanueva;
        this.idlecturaanterior = idlecturaanterior;
    }

}
