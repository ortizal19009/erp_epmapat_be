package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tramitenuevo")

public class TramiteNuevo {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtramitenuevo;
	private String direccion;
	private String nrocasa;
	private String nrodepar;
	private String referencia;
	private String barrio;
	private Long tipopredio;
	private Long presentacedula;
	private Long presentaescritura;
	private Long solicitaagua;
	private Long solicitaalcantarillado;
	private Long aprobadoagua;
	private Long aprobadoalcantarillado;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechainspeccion")
	private Date fechainspeccion;
	private Long medidorempresa;
	private String medidormarca;
	private String medidornumero;
	private Long medidornroesferas;
	private String tuberiaprincipal;
	private Long tipovia;
	private Long codmedidor;
	private Long codmedidorvecino;
	private Long secuencia;
	private String inspector;
	private Long areaconstruccion;
	private String notificado;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechanotificacion")
	private Date fechanotificacion;
	private String observaciones;
	private Long estado;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechafinalizacion")
	private Date fechafinalizacion;
	private Long medidordiametro;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idcategoria_categorias")
	private Categorias idcategoria_categorias;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idaguatramite_aguatramite")
	private AguaTramite idaguatramite_aguatramite;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;
	public TramiteNuevo() {
		super();
		
	}
	public TramiteNuevo(Long idtramitenuevo, String direccion, String nrocasa, String nrodepar, String referencia,
			String barrio, Long tipopredio, Long presentacedula,Long presentaescritura, Long solicitaagua, Long solicitaalcantarillado,
			Long aprobadoagua, Long aprobadoalcantarillado, Date fechainspeccion, Long medidorempresa,
			String medidormarca, String medidornumero, Long medidornroesferas, String tuberiaprincipal, Long tipovia,
			Long codmedidor, Long codmedidorvecino, Long secuencia, String inspector, Long areaconstruccion,
			String notificado, Date fechanotificacion, String observaciones, Long estado, Date fechafinalizacion,
			Long medidordiametro, Categorias idcategoria_categorias, AguaTramite idaguatramite_aguatramite,
			Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idtramitenuevo = idtramitenuevo;
		this.direccion = direccion;
		this.nrocasa = nrocasa;
		this.nrodepar = nrodepar;
		this.referencia = referencia;
		this.barrio = barrio;
		this.tipopredio = tipopredio;
		this.presentacedula = presentacedula;
		this.presentaescritura = presentaescritura;
		this.solicitaagua = solicitaagua;
		this.solicitaalcantarillado = solicitaalcantarillado;
		this.aprobadoagua = aprobadoagua;
		this.aprobadoalcantarillado = aprobadoalcantarillado;
		this.fechainspeccion = fechainspeccion;
		this.medidorempresa = medidorempresa;
		this.medidormarca = medidormarca;
		this.medidornumero = medidornumero;
		this.medidornroesferas = medidornroesferas;
		this.tuberiaprincipal = tuberiaprincipal;
		this.tipovia = tipovia;
		this.codmedidor = codmedidor;
		this.codmedidorvecino = codmedidorvecino;
		this.secuencia = secuencia;
		this.inspector = inspector;
		this.areaconstruccion = areaconstruccion;
		this.notificado = notificado;
		this.fechanotificacion = fechanotificacion;
		this.observaciones = observaciones;
		this.estado = estado;
		this.fechafinalizacion = fechafinalizacion;
		this.medidordiametro = medidordiametro;
		this.idcategoria_categorias = idcategoria_categorias;
		this.idaguatramite_aguatramite = idaguatramite_aguatramite;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	public Long getIdtramitenuevo() {
		return idtramitenuevo;
	}
	public void setIdtramitenuevo(Long idtramitenuevo) {
		this.idtramitenuevo = idtramitenuevo;
	}
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getNrocasa() {
		return nrocasa;
	}
	public void setNrocasa(String nrocasa) {
		this.nrocasa = nrocasa;
	}
	public String getNrodepar() {
		return nrodepar;
	}
	public void setNrodepar(String nrodepar) {
		this.nrodepar = nrodepar;
	}
	public String getReferencia() {
		return referencia;
	}
	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}
	public String getBarrio() {
		return barrio;
	}
	public void setBarrio(String barrio) {
		this.barrio = barrio;
	}
	public Long getTipopredio() {
		return tipopredio;
	}
	public void setTipopredio(Long tipopredio) {
		this.tipopredio = tipopredio;
	}
	public Long getPresentacedula() {
		return presentacedula;
	}
	public void setPresentacedula(Long presentacedula) {
		this.presentacedula = presentacedula;
	}
	public Long getSolicitaagua() {
		return solicitaagua;
	}
	public void setSolicitaagua(Long solicitaagua) {
		this.solicitaagua = solicitaagua;
	}
	public Long getSolicitaalcantarillado() {
		return solicitaalcantarillado;
	}
	public void setSolicitaalcantarillado(Long solicitaalcantarillado) {
		this.solicitaalcantarillado = solicitaalcantarillado;
	}
	public Long getAprobadoagua() {
		return aprobadoagua;
	}
	public void setAprobadoagua(Long aprobadoagua) {
		this.aprobadoagua = aprobadoagua;
	}
	public Long getAprobadoalcantarillado() {
		return aprobadoalcantarillado;
	}
	public void setAprobadoalcantarillado(Long aprobadoalcantarillado) {
		this.aprobadoalcantarillado = aprobadoalcantarillado;
	}
	public Date getFechainspeccion() {
		return fechainspeccion;
	}
	public void setFechainspeccion(Date fechainspeccion) {
		this.fechainspeccion = fechainspeccion;
	}
	public Long getMedidorempresa() {
		return medidorempresa;
	}
	public void setMedidorempresa(Long medidorempresa) {
		this.medidorempresa = medidorempresa;
	}
	public String getMedidormarca() {
		return medidormarca;
	}
	public void setMedidormarca(String medidormarca) {
		this.medidormarca = medidormarca;
	}
	public String getMedidornumero() {
		return medidornumero;
	}
	public void setMedidornumero(String medidornumero) {
		this.medidornumero = medidornumero;
	}
	public Long getMedidornroesferas() {
		return medidornroesferas;
	}
	public void setMedidornroesferas(Long medidornroesferas) {
		this.medidornroesferas = medidornroesferas;
	}
	public String getTuberiaprincipal() {
		return tuberiaprincipal;
	}
	public void setTuberiaprincipal(String tuberiaprincipal) {
		this.tuberiaprincipal = tuberiaprincipal;
	}
	public Long getTipovia() {
		return tipovia;
	}
	public void setTipovia(Long tipovia) {
		this.tipovia = tipovia;
	}
	public Long getCodmedidor() {
		return codmedidor;
	}
	public void setCodmedidor(Long codmedidor) {
		this.codmedidor = codmedidor;
	}
	public Long getCodmedidorvecino() {
		return codmedidorvecino;
	}
	public void setCodmedidorvecino(Long codmedidorvecino) {
		this.codmedidorvecino = codmedidorvecino;
	}
	public Long getSecuencia() {
		return secuencia;
	}
	public void setSecuencia(Long secuencia) {
		this.secuencia = secuencia;
	}
	public String getInspector() {
		return inspector;
	}
	public void setInspector(String inspector) {
		this.inspector = inspector;
	}
	public Long getAreaconstruccion() {
		return areaconstruccion;
	}
	public void setAreaconstruccion(Long areaconstruccion) {
		this.areaconstruccion = areaconstruccion;
	}
	public String getNotificado() {
		return notificado;
	}
	public void setNotificado(String notificado) {
		this.notificado = notificado;
	}
	public Date getFechanotificacion() {
		return fechanotificacion;
	}
	public void setFechanotificacion(Date fechanotificacion) {
		this.fechanotificacion = fechanotificacion;
	}
	public String getObservaciones() {
		return observaciones;
	}
	public void setObservaciones(String observaciones) {
		this.observaciones = observaciones;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public Date getFechafinalizacion() {
		return fechafinalizacion;
	}
	public void setFechafinalizacion(Date fechafinalizacion) {
		this.fechafinalizacion = fechafinalizacion;
	}
	public Long getMedidordiametro() {
		return medidordiametro;
	}
	public void setMedidordiametro(Long medidordiametro) {
		this.medidordiametro = medidordiametro;
	}
	public Categorias getIdcategoria_categorias() {
		return idcategoria_categorias;
	}
	public void setIdcategoria_categorias(Categorias idcategoria_categorias) {
		this.idcategoria_categorias = idcategoria_categorias;
	}
	public AguaTramite getIdaguatramite_aguatramite() {
		return idaguatramite_aguatramite;
	}
	public void setIdaguatramite_aguatramite(AguaTramite idaguatramite_aguatramite) {
		this.idaguatramite_aguatramite = idaguatramite_aguatramite;
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
	public Long getPresentaescritura() {
		return presentaescritura;
	}
	public void setPresentaescritura(Long presentaescritura) {
		this.presentaescritura = presentaescritura;
	}
	

}
