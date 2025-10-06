package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tabla5")

public class Tabla5 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtabla5;
    private String codporcentaje;
    private Float porcentaje;
    private String tipoiva;
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

    public Tabla5() {
        super();
    }

    public Tabla5(Long idtabla5, String codporcentaje, Float porcentaje, String tipoiva, Integer usucrea, Date feccrea,
            Integer usumodi, Date fecmodi) {
        super();
        this.idtabla5 = idtabla5;
        this.codporcentaje = codporcentaje;
        this.porcentaje = porcentaje;
        this.tipoiva = tipoiva;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtabla5() {
        return idtabla5;
    }

    public void setIdtabla5(Long idtabla5) {
        this.idtabla5 = idtabla5;
    }

    public String getCodporcentaje() {
        return codporcentaje;
    }

    public void setCodporcentaje(String codporcentaje) {
        this.codporcentaje = codporcentaje;
    }

    public Float getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(Float porcentaje) {
        this.porcentaje = porcentaje;
    }

    public String getTipoiva() {
        return tipoiva;
    }

    public void setTipoiva(String tipoiva) {
        this.tipoiva = tipoiva;
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
