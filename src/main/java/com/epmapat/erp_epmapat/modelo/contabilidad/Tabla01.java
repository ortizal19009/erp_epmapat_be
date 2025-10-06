package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tabla01")
public class Tabla01 {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)

    private Long idtabla01;
	private String codsustento;
	private String  nomsustento;
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
    
    public Tabla01() {
		super();
    }
    public Tabla01(Long idtabla01, String codsustento, String nomsustento, Integer usucrea, Date feccrea,
            Integer usumodi, Date fecmodi) {
                super();
        this.idtabla01 = idtabla01;
        this.codsustento = codsustento;
        this.nomsustento = nomsustento;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }
    public Long getIdtabla01() {
        return idtabla01;
    }
    public void setIdtabla01(Long idtabla01) {
        this.idtabla01 = idtabla01;
    }
    public String getCodsustento() {
        return codsustento;
    }
    public void setCodsustento(String codsustento) {
        this.codsustento = codsustento;
    }
    public String getNomsustento() {
        return nomsustento;
    }
    public void setNomsustento(String nomsustento) {
        this.nomsustento = nomsustento;
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
