package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tabla15")

public class Tabla15 {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idtabla15;
    private String codtabla15;
    private String nomtabla15;
    private Integer  usucrea;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "feccrea")
    private Date feccrea;
    private Integer usumodi;
    @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecmodi")
    private Date fecmodi;

    public Tabla15() {
        super();
    }

    public Tabla15(Long idtabla15, String codtabla15, String nomtabla15, Integer usucrea, Date feccrea, Integer usumodi,
            Date fecmodi) {
                super();
        this.idtabla15 = idtabla15;
        this.codtabla15 = codtabla15;
        this.nomtabla15 = nomtabla15;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    public Long getIdtabla15() {
        return idtabla15;
    }

    public void setIdtabla15(Long idtabla15) {
        this.idtabla15 = idtabla15;
    }

    public String getCodtabla15() {
        return codtabla15;
    }

    public void setCodtabla15(String codtabla15) {
        this.codtabla15 = codtabla15;
    }

    public String getNomtabla15() {
        return nomtabla15;
    }

    public void setNomtabla15(String nomtabla15) {
        this.nomtabla15 = nomtabla15;
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
