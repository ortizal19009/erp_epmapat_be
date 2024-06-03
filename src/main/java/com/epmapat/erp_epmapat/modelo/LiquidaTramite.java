package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "liquidatrami")

public class LiquidaTramite {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idliquidatrami;
	private Long cuota;
	private Float valor;
	private Long usuarioeliminacion;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaeliminacion")
	private Date fechaeliminacion;
	private String razoneliminacion;
	private Long estado;
	private String observacion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idtramite_tramites")
	private CtramitesM idtramite_tramites;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idfactura_facturas")
	private Facturas idfactura_facturas;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	public LiquidaTramite() {
		super();
		
	}
	public LiquidaTramite(Long idliquidatrami, Long cuota, Float valor, Long usuarioeliminacion, Date fechaeliminacion,
			String razoneliminacion, Long estado, String observacion, CtramitesM idtramite_tramites,
			Facturas idfactura_facturas, Long usucrea, Date feccrea) {
		super();
		this.idliquidatrami = idliquidatrami;
		this.cuota = cuota;
		this.valor = valor;
		this.usuarioeliminacion = usuarioeliminacion;
		this.fechaeliminacion = fechaeliminacion;
		this.razoneliminacion = razoneliminacion;
		this.estado = estado;
		this.observacion = observacion;
		this.idtramite_tramites = idtramite_tramites;
		this.idfactura_facturas = idfactura_facturas;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
	}
	public Long getIdliquidatrami() {
		return idliquidatrami;
	}
	public void setIdliquidatrami(Long idliquidatrami) {
		this.idliquidatrami = idliquidatrami;
	}
	public Long getCuota() {
		return cuota;
	}
	public void setCuota(Long cuota) {
		this.cuota = cuota;
	}
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
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
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public CtramitesM getIdtramite_tramites() {
		return idtramite_tramites;
	}
	public void setIdtramite_tramites(CtramitesM idtramite_tramites) {
		this.idtramite_tramites = idtramite_tramites;
	}
	public Facturas getIdfactura_facturas() {
		return idfactura_facturas;
	}
	public void setIdfactura_facturas(Facturas idfactura_facturas) {
		this.idfactura_facturas = idfactura_facturas;
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
		
}
