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
    private Long idlectura;

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

    public Long getIdlectura() {
        return idlectura;
    }

    public void setIdlectura(Long idlectura) {
        this.idlectura = idlectura;
    }

    public EmisionIndividual(Long idemisionindividual, Long idemision, Long idlectura) {
        this.idemisionindividual = idemisionindividual;
        this.idemision = idemision;
        this.idlectura = idlectura;
    }

    public EmisionIndividual() {
        super();
    }
}
