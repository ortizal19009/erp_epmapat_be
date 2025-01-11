package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.*;

@Entity
@Table(name = "tabla10")

public class Tabla10 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idtabla10;
    private String codretair;
    private String conceptoretair;
    private Float porcretair;
    private String codcue;

    public Tabla10() {
        super();
    }

    // public Tabla10(Long idtabla10, String codretair, String conceptoretair, Float porcretair, String codcue) {
    //     super();
    //     this.idtabla10 = idtabla10;
    //     this.codretair = codretair;
    //     this.conceptoretair = conceptoretair;
    //     this.porcretair = porcretair;
    //     this.codcue = codcue;
    // }

    public Long getIdtabla10() {
        return idtabla10;
    }

    public void setIdtabla10(Long idtabla10) {
        this.idtabla10 = idtabla10;
    }

    public String getCodretair() {
        return codretair;
    }

    public void setCodretair(String codretair) {
        this.codretair = codretair;
    }

    public String getConceptoretair() {
        return conceptoretair;
    }

    public void setConceptoretair(String conceptoretair) {
        this.conceptoretair = conceptoretair;
    }

    public Float getPorcretair() {
        return porcretair;
    }

    public void setPorcretair(Float porcretair) {
        this.porcretair = porcretair;
    }

    public String getCodcue() {
        return codcue;
    }

    public void setCodcue(String codcue) {
        this.codcue = codcue;
    }

}