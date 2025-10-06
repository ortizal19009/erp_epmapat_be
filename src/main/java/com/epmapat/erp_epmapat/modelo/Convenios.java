package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
    private Integer nroconvenio;
    private BigDecimal totalconvenio;
    private Short cuotas;
    private BigDecimal cuotainicial;
    private BigDecimal pagomensual;
    private BigDecimal cuotafinal;
    private String observaciones;
    private Long usuarioeliminacion;
    private LocalDate fechaeliminacion;
    private String razoneliminacion;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private Timestamp fecmodi;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idabonado")
    private Abonados idabonado;

    public Convenios() {
        super();
    }

    /* ================== GETTERS Y SETTERS ========== */
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

    public BigDecimal getTotalconvenio() {
        return totalconvenio;
    }

    public void setTotalconvenio(BigDecimal totalconvenio) {
        this.totalconvenio = totalconvenio;
    }

    public BigDecimal getCuotainicial() {
        return cuotainicial;
    }

    public void setCuotainicial(BigDecimal cuotainicial) {
        this.cuotainicial = cuotainicial;
    }

    public BigDecimal getCuotafinal() {
        return cuotafinal;
    }

    public void setCuotafinal(BigDecimal cuotafinal) {
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

    public LocalDate getFechaeliminacion() {
        return fechaeliminacion;
    }

    public void setFechaeliminacion(LocalDate fechaeliminacion) {
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

    public LocalDate getFeccrea() {
        return feccrea;
    }

    public void setFeccrea(LocalDate feccrea) {
        this.feccrea = feccrea;
    }

    public Long getUsumodi() {
        return usumodi;
    }

    public void setUsumodi(Long usumodi) {
        this.usumodi = usumodi;
    }

    public Timestamp getFecmodi() {
        return fecmodi;
    }

    public void setFecmodi(Timestamp fecmodi) {
        this.fecmodi = fecmodi;
    }

    public Abonados getIdabonado() {
        return idabonado;
    }

    public void setIdabonado(Abonados idabonado) {
        this.idabonado = idabonado;
    }

    public Short getCuotas() {
        return cuotas;
    }

    public void setCuotas(Short cuotas) {
        this.cuotas = cuotas;
    }

    public BigDecimal getPagomensual() {
        return pagomensual;
    }

    public void setPagomensual(BigDecimal pagomensual) {
        this.pagomensual = pagomensual;
    }

}
