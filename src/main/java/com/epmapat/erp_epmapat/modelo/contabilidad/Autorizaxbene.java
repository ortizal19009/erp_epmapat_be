package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "autorizaxbene")

public class Autorizaxbene {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idautoriza;
    private String numautoriza;
    private String numserie;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fechacaduca")
    private Date fechacaduca;
    private String facdesde;
    private String fachasta;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idbene")
    private Beneficiarios idbene;
    private Integer usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Integer usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;

    public Autorizaxbene() {
        super();
    }

    public Autorizaxbene(Long idautoriza, String numautoriza, String numserie, Date fechacaduca, String facdesde,
            String fachasta, Beneficiarios idbene, Integer usucrea, Date feccrea, Integer usumodi, Date fecmodi) {
        super();
        this.idautoriza = idautoriza;
        this.numautoriza = numautoriza;
        this.numserie = numserie;
        this.fechacaduca = fechacaduca;
        this.facdesde = facdesde;
        this.fachasta = fachasta;
        this.idbene = idbene;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdautoriza() {
        return idautoriza;
    }

    public void setIdautoriza(Long idautoriza) {
        this.idautoriza = idautoriza;
    }

    public String getNumautoriza() {
        return numautoriza;
    }

    public void setNumautoriza(String numautoriza) {
        this.numautoriza = numautoriza;
    }

    public String getNumserie() {
        return numserie;
    }

    public void setNumserie(String numserie) {
        this.numserie = numserie;
    }

    public Date getFechacaduca() {
        return fechacaduca;
    }

    public void setFechacaduca(Date fechacaduca) {
        this.fechacaduca = fechacaduca;
    }

    public String getFacdesde() {
        return facdesde;
    }

    public void setFacdesde(String facdesde) {
        this.facdesde = facdesde;
    }

    public String getFachasta() {
        return fachasta;
    }

    public void setFachasta(String fachasta) {
        this.fachasta = fachasta;
    }

    public Beneficiarios getIdbene() {
        return idbene;
    }

    public void setIdbene(Beneficiarios idbene) {
        this.idbene = idbene;
    }

    public Integer getUsucrea() {
        return usucrea;
    }

    public void setUsucrea(Integer usucrea) {
        this.usucrea = usucrea;
    }

    public Date getFeccrea() {
        return feccrea;
    }

    public void setFeccrea(Date feccrea) {
        this.feccrea = feccrea;
    }

    public Integer getUsumodi() {
        return usumodi;
    }

    public void setUsumodi(Integer usumodi) {
        this.usumodi = usumodi;
    }

    public Date getFecmodi() {
        return fecmodi;
    }

    public void setFecmodi(Date fecmodi) {
        this.fecmodi = fecmodi;
    }

}
