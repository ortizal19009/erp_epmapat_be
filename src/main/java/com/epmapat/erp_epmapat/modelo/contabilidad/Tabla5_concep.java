package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tabla5_concep")

public class Tabla5_concep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idtabla5c;
    private String conceptoiva;
    private String casillero104;
    private String codcue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtabla5")
    private Tabla5 idtabla5;

    private Integer tipo100;
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

    public Tabla5_concep() {
        super();
    }

    public Tabla5_concep(Long idtabla5c, String conceptoiva, String casillero104, String codcue, Tabla5 idtabla5,
            Integer tipo100, Integer usucrea, Date feccrea, Integer usumodi, Date fecmodi) {
                 super();
        this.idtabla5c = idtabla5c;
        this.conceptoiva = conceptoiva;
        this.casillero104 = casillero104;
        this.codcue = codcue;
        this.idtabla5 = idtabla5;
        this.tipo100 = tipo100;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtabla5c() {
        return idtabla5c;
    }

    public void setIdtabla5c(Long idtabla5c) {
        this.idtabla5c = idtabla5c;
    }

    public String getConceptoiva() {
        return conceptoiva;
    }

    public void setConceptoiva(String conceptoiva) {
        this.conceptoiva = conceptoiva;
    }

    public String getCasillero104() {
        return casillero104;
    }

    public void setCasillero104(String casillero104) {
        this.casillero104 = casillero104;
    }

    public String getCodcue() {
        return codcue;
    }

    public void setCodcue(String codcue) {
        this.codcue = codcue;
    }

    public Tabla5 getIdtabla5() {
        return idtabla5;
    }

    public void setIdtabla5(Tabla5 idtabla5) {
        this.idtabla5 = idtabla5;
    }

    public Integer getTipo100() {
        return tipo100;
    }

    public void setTipo100(Integer tipo100) {
        this.tipo100 = tipo100;
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
