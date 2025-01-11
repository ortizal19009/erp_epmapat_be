package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tabla17")

public class Tabla17 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtabla17;
    private String porcentaje;
    private Integer codigo;
    private Float porciva;
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

    public Tabla17() {
        super();
    }

    public Tabla17(Long idtabla17, String porcentaje, Integer codigo, Float porciva, Integer usucrea, Date feccrea,
            Integer usumodi, Date fecmodi) {
        super();
        this.idtabla17 = idtabla17;
        this.porcentaje = porcentaje;
        this.codigo = codigo;
        this.porciva = porciva;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtabla17() {
        return idtabla17;
    }

    public void setIdtabla17(Long idtabla17) {
        this.idtabla17 = idtabla17;
    }

    public String getPorcentaje() {
        return porcentaje;
    }

    public void setPorcentaje(String porcentaje) {
        this.porcentaje = porcentaje;
    }

    public Integer getCodigo() {
        return codigo;
    }

    public void setCodigo(Integer codigo) {
        this.codigo = codigo;
    }

    public Float getPorciva() {
        return porciva;
    }

    public void setPorciva(Float porciva) {
        this.porciva = porciva;
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
