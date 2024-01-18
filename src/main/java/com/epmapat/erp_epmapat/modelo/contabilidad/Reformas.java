package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "reformas")

public class Reformas {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idrefo;
    private Long numero;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecha")
    private Date fecha;
    private String tipo;
    private Float valor;
    private String concepto;
    private String numdoc;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "iddocumento")
    private Documentos iddocumento;

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
    
    public Long getIdrefo() {
        return idrefo;
    }
    public void setIdrefo(Long idrefo) {
        this.idrefo = idrefo;
    }
    public Long getNumero() {
        return numero;
    }
    public void setNumero(Long numero) {
        this.numero = numero;
    }
    public Date getFecha() {
        return fecha;
    }
    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public Float getValor() {
        return valor;
    }
    public void setValor(Float valor) {
        this.valor = valor;
    }
    public String getConcepto() {
        return concepto;
    }
    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }
    public String getNumdoc() {
        return numdoc;
    }
    public void setNumdoc(String numdoc) {
        this.numdoc = numdoc;
    }
    public Documentos getIddocumento() {
        return iddocumento;
    }
    public void setIddocumento(Documentos iddocumento) {
        this.iddocumento = iddocumento;
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
