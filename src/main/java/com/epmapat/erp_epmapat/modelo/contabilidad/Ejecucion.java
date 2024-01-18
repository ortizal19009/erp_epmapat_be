package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "ejecucion")
public class Ejecucion {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idejecu;
    private String codpar;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecha_eje")
    private Date fecha_eje;
    private Integer tipeje;
    private Float modifi;
    private Float prmiso;
    private Float totdeven;
    private Float devengado;
    private Float cobpagado;
    private String concepto;
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
    private Long idrefo;
    private Long idtrami;
    private Long idparxcer;
    private Long idasiento;
    private Long idtransa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idpresupue")
    private Presupue idpresupue;
    // private Long idpresupue;

    private Long idprmiso;
    private Long idevenga;
    
    public Long getIdejecu() {
        return idejecu;
    }
    public void setIdejecu(Long idejecu) {
        this.idejecu = idejecu;
    }
    public String getCodpar() {
        return codpar;
    }
    public void setCodpar(String codpar) {
        this.codpar = codpar;
    }
    public Date getFecha_eje() {
        return fecha_eje;
    }
    public void setFecha_eje(Date fecha_eje) {
        this.fecha_eje = fecha_eje;
    }
    public Integer getTipeje() {
        return tipeje;
    }
    public void setTipeje(Integer tipeje) {
        this.tipeje = tipeje;
    }
    public Float getModifi() {
        return modifi;
    }
    public void setModifi(Float modifi) {
        this.modifi = modifi;
    }
    public Float getPrmiso() {
        return prmiso;
    }
    public void setPrmiso(Float prmiso) {
        this.prmiso = prmiso;
    }
    public Float getTotdeven() {
        return totdeven;
    }
    public void setTotdeven(Float totdeven) {
        this.totdeven = totdeven;
    }
    public Float getDevengado() {
        return devengado;
    }
    public void setDevengado(Float devengado) {
        this.devengado = devengado;
    }
    public Float getCobpagado() {
        return cobpagado;
    }
    public void setCobpagado(Float cobpagado) {
        this.cobpagado = cobpagado;
    }
    public String getConcepto() {
        return concepto;
    }
    public void setConcepto(String concepto) {
        this.concepto = concepto;
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
    public Long getIdrefo() {
        return idrefo;
    }
    public void setIdrefo(Long idrefo) {
        this.idrefo = idrefo;
    }
    public Long getIdtrami() {
        return idtrami;
    }
    public void setIdtrami(Long idtrami) {
        this.idtrami = idtrami;
    }
    public Long getIdparxcer() {
        return idparxcer;
    }
    public void setIdparxcer(Long idparxcer) {
        this.idparxcer = idparxcer;
    }
    public Long getIdasiento() {
        return idasiento;
    }
    public void setIdasiento(Long idasiento) {
        this.idasiento = idasiento;
    }
    public Long getIdtransa() {
        return idtransa;
    }
    public void setIdtransa(Long idtransa) {
        this.idtransa = idtransa;
    }
    public Presupue getIdpresupue() {
        return idpresupue;
    }
    // public Long getIdpresupue() {
    //     return idpresupue;
    // }
    public void setIdpresupue(Presupue idpresupue) {
        this.idpresupue = idpresupue;
    }
    // public void setIdpresupue(Long idpresupue) {
    //     this.idpresupue = idpresupue;
    // }

    public Long getIdprmiso() {
        return idprmiso;
    }
    public void setIdprmiso(Long idprmiso) {
        this.idprmiso = idprmiso;
    }
    public Long getIdevenga() {
        return idevenga;
    }
    public void setIdevenga(Long idevenga) {
        this.idevenga = idevenga;
    }
    
}
