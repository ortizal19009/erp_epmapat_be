package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

//import org.hibernate.annotations.NaturalId;

@Entity
@Table(name = "convenios")

public class Convenios implements Serializable {
 
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idconvenio;
    private String nroautorizacion;
    private String referencia;
    private Integer estado;
    // @NaturalId
    private Integer nroconvenio;
    //Integer nroconvenio;
    private Float totalconvenio;
    private Float cuotainicial;
    private Float cuotafinal;
    private String observaciones;
    private Long usuarioeliminacion;
    private Date fechaeliminacion;
    private String razoneliminacion;
    private Long usucrea;
    private Date feccrea;
    private Long usumodi;
    private Date fecmodi;
    
    public Convenios() {
        super();
    }

    public Convenios(Long idconvenio, String nroautorizacion, String referencia, Integer estado, Integer nroconvenio,
            Float totalconvenio, Float cuotainicial, Float cuotafinal, String observaciones, Long usuarioeliminacion,
            Date fechaeliminacion, String razoneliminacion, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
        this.idconvenio = idconvenio;
        this.nroautorizacion = nroautorizacion;
        this.referencia = referencia;
        this.estado = estado;
        this.nroconvenio = nroconvenio;
        this.totalconvenio = totalconvenio;
        this.cuotainicial = cuotainicial;
        this.cuotafinal = cuotafinal;
        this.observaciones = observaciones;
        this.usuarioeliminacion = usuarioeliminacion;
        this.fechaeliminacion = fechaeliminacion;
        this.razoneliminacion = razoneliminacion;
        this.usucrea = usucrea;
        this.feccrea = feccrea;
        this.usumodi = usumodi;
        this.fecmodi = fecmodi;
    }

    /* ================== GETTERS Y SETTERS ==========*/
    public Long getIdconvenio() {
        return idconvenio;
    }
    public void setIdconvenio(Long idconvenio) {
        this.idconvenio = idconvenio;
    }
    public String getNroautorizacion() {
        return nroautorizacion;
    }
    public void setNroautorizacion(String nroautorizacion) {
        this.nroautorizacion = nroautorizacion;
    }
    public String getReferencia() {
        return referencia;
    }
    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }
    public Integer getEstado() {
        return estado;
    }
    public void setEstado(Integer estado) {
        this.estado = estado;
    }
    public Integer getNroconvenio() {
        return nroconvenio;
    }
    public void setNroconvenio(Integer nroconvenio) {
        this.nroconvenio = nroconvenio;
    }
    public Float getTotalconvenio() {
        return totalconvenio;
    }
    public void setTotalconvenio(Float totalconvenio) {
        this.totalconvenio = totalconvenio;
    }
    public Float getCuotainicial() {
        return cuotainicial;
    }
    public void setCuotainicial(Float cuotainicial) {
        this.cuotainicial = cuotainicial;
    }
    public Float getCuotafinal() {
        return cuotafinal;
    }
    public void setCuotafinal(Float cuotafinal) {
        this.cuotafinal = cuotafinal;
    }
    public String getObservaciones() {
        return observaciones;
    }
    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
    public Long getUsuarioeliminacion() {
        return usuarioeliminacion;
    }
    public void setUsuarioeliminacion(Long usuarioeliminacion) {
        this.usuarioeliminacion = usuarioeliminacion;
    }
    public Date getFechaeliminacion() {
        return fechaeliminacion;
    }
    public void setFechaeliminacion(Date fechaeliminacion) {
        this.fechaeliminacion = fechaeliminacion;
    }
    public String getRazoneliminacion() {
        return razoneliminacion;
    }
    public void setRazoneliminacion(String razoneliminacion) {
        this.razoneliminacion = razoneliminacion;
    }
    public Long getUsucrea() {
        return usucrea;
    }
    public void setUsucrea(Long usucrea) {
        this.usucrea = usucrea;
    }
    public Date getFeccrea() {
        return feccrea;
    }
    public void setFeccrea(Date feccrea) {
        this.feccrea = feccrea;
    }
    public Long getUsumodi() {
        return usumodi;
    }
    public void setUsumodi(Long usumodi) {
        this.usumodi = usumodi;
    }
    public Date getFecmodi() {
        return fecmodi;
    }
    public void setFecmodi(Date fecmodi) {
        this.fecmodi = fecmodi;
    }

}
