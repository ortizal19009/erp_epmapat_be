package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "reclamos")

public class Reclamos {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idreclamo;
	private String observacion;
	private Long referencia;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaasignacion")
	private Date fechaasignacion; 
	private Long estado;
	private String referenciadireccion; 
	private String piso; 
	private String departamento; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechamaxcontesta")
	private Date fechamaxcontesta; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column ( name = "fechacontesta")
	private Date fechacontesta;
	private String contestacion;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column ( name = "fechaterminacion")
	private Date fechaterminacion;
	private String responsablereclamo; 
	private String modulo; 
	private String notificacion; 
	private Long estadonotificacion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtpreclamo_tpreclamo")
	private TpReclamoM idtpreclamo_tpreclamo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmodulo_modulos")
	private Modulos idmodulo_modulos;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name="feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name="fecmodi")
	private Date fecmodi;
	
	public Reclamos() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Reclamos(Long idreclamo, String observacion, Long referencia, Date fechaasignacion, Long estado,
			String referenciadireccion, String piso, String departamento, Date fechamaxcontesta, Date fechacontesta,
			String contestacion, Date fechaterminacion, String responsablereclamo, String modulo, String notificacion,
			Long estadonotificacion, TpReclamoM idtpreclamo_tpreclamo, Modulos idmodulo_modulos, Long usucrea,
			Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idreclamo = idreclamo;
		this.observacion = observacion;
		this.referencia = referencia;
		this.fechaasignacion = fechaasignacion;
		this.estado = estado;
		this.referenciadireccion = referenciadireccion;
		this.piso = piso;
		this.departamento = departamento;
		this.fechamaxcontesta = fechamaxcontesta;
		this.fechacontesta = fechacontesta;
		this.contestacion = contestacion;
		this.fechaterminacion = fechaterminacion;
		this.responsablereclamo = responsablereclamo;
		this.modulo = modulo;
		this.notificacion = notificacion;
		this.estadonotificacion = estadonotificacion;
		this.idtpreclamo_tpreclamo = idtpreclamo_tpreclamo;
		this.idmodulo_modulos = idmodulo_modulos;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	public Long getIdreclamo() {
		return idreclamo;
	}
	public void setIdreclamo(Long idreclamo) {
		this.idreclamo = idreclamo;
	}
	public String getObservacion() {
		return observacion;
	}
	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}
	public Long getReferencia() {
		return referencia;
	}
	public void setReferencia(Long referencia) {
		this.referencia = referencia;
	}
	public Date getFechaasignacion() {
		return fechaasignacion;
	}
	public void setFechaasignacion(Date fechaasignacion) {
		this.fechaasignacion = fechaasignacion;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getReferenciadireccion() {
		return referenciadireccion;
	}
	public void setReferenciadireccion(String referenciadireccion) {
		this.referenciadireccion = referenciadireccion;
	}
	public String getPiso() {
		return piso;
	}
	public void setPiso(String piso) {
		this.piso = piso;
	}
	public String getDepartamento() {
		return departamento;
	}
	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}
	public Date getFechamaxcontesta() {
		return fechamaxcontesta;
	}
	public void setFechamaxcontesta(Date fechamaxcontesta) {
		this.fechamaxcontesta = fechamaxcontesta;
	}
	public Date getFechacontesta() {
		return fechacontesta;
	}
	public void setFechacontesta(Date fechacontesta) {
		this.fechacontesta = fechacontesta;
	}
	public String getContestacion() {
		return contestacion;
	}
	public void setContestacion(String contestacion) {
		this.contestacion = contestacion;
	}
	public Date getFechaterminacion() {
		return fechaterminacion;
	}
	public void setFechaterminacion(Date fechaterminacion) {
		this.fechaterminacion = fechaterminacion;
	}
	public String getResponsablereclamo() {
		return responsablereclamo;
	}
	public void setResponsablereclamo(String responsablereclamo) {
		this.responsablereclamo = responsablereclamo;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getNotificacion() {
		return notificacion;
	}
	public void setNotificacion(String notificacion) {
		this.notificacion = notificacion;
	}
	public Long getEstadonotificacion() {
		return estadonotificacion;
	}
	public void setEstadonotificacion(Long estadonotificacion) {
		this.estadonotificacion = estadonotificacion;
	}
	public TpReclamoM getIdtpreclamo_tpreclamo() {
		return idtpreclamo_tpreclamo;
	}
	public void setIdtpreclamo_tpreclamo(TpReclamoM idtpreclamo_tpreclamo) {
		this.idtpreclamo_tpreclamo = idtpreclamo_tpreclamo;
	}
	public Modulos getIdmodulo_modulos() {
		return idmodulo_modulos;
	}
	public void setIdmodulo_modulos(Modulos idmodulo_modulos) {
		this.idmodulo_modulos = idmodulo_modulos;
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
