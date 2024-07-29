package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "facturas")
public class Facturas implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfactura;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmodulo")
	private Modulos idmodulo;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente")
	private Clientes idcliente;
	private String nrofactura;
	private Long porcexoneracion;
	private String razonexonera;
	private BigDecimal totaltarifa;
	private Integer pagado;
	private Long usuariocobro;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE_TIME)
	// @Column(name = "fechacobro")
	private LocalDate fechacobro;
	private Long estado;
	private Long usuarioanulacion;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fechaanulacion")
	private LocalDate fechaanulacion;
	private String razonanulacion;
	private Long usuarioeliminacion;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso= ISO.DATE)
	// @Column ( name = "fechaeliminacion")
	private LocalDate fechaeliminacion;
	private String razoneliminacion;
	private Long conveniopago;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso= ISO.DATE)
	// @Column(name = "fechaconvenio")
	private LocalDate fechaconvenio;
	private Long estadoconvenio;
	private Long formapago;
	private String refeformapago;
	private String horacobro;
	private Long usuariotransferencia;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fechatransferencia")
	private LocalDate fechatransferencia;
	private Long usucrea;

	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso=ISO.DATE)
	// @Column(name = "feccrea")
	private LocalDate feccrea;
	private Long usumodi;
	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "fecmodi")
	private LocalDate fecmodi;
	private BigDecimal valorbase;
	private Long idabonado;
	private BigDecimal interescobrado;
	private BigDecimal swiva;
	private Boolean swcondonar; 

	public BigDecimal getInterescobrado() {
		return interescobrado;
	}

	public void setInterescobrado(BigDecimal interescobrado) {
		this.interescobrado = interescobrado;
	}

	public Facturas() {
		super();
	}
	
	public Long getIdfactura() {
		return idfactura;
	}

	public void setIdfactura(Long idfactura) {
		this.idfactura = idfactura;
	}

	public Modulos getIdmodulo() {
		return idmodulo;
	}

	public void setIdmodulo(Modulos idmodulo) {
		this.idmodulo = idmodulo;
	}

	public Clientes getIdcliente() {
		return idcliente;
	}

	public void setIdcliente(Clientes idcliente) {
		this.idcliente = idcliente;
	}

	public String getNrofactura() {
		return nrofactura;
	}

	public void setNrofactura(String nrofactura) {
		this.nrofactura = nrofactura;
	}

	public Long getPorcexoneracion() {
		return porcexoneracion;
	}

	public void setPorcexoneracion(Long porcexoneracion) {
		this.porcexoneracion = porcexoneracion;
	}

	public String getRazonexonera() {
		return razonexonera;
	}

	public void setRazonexonera(String razonexonera) {
		this.razonexonera = razonexonera;
	}

	public BigDecimal getTotaltarifa() {
		return totaltarifa;
	}

	public void setTotaltarifa(BigDecimal totaltarifa) {
		this.totaltarifa = totaltarifa;
	}

	public Integer getPagado() {
		return pagado;
	}

	public void setPagado(Integer pagado) {
		this.pagado = pagado;
	}

	public Long getUsuariocobro() {
		return usuariocobro;
	}

	public void setUsuariocobro(Long usuariocobro) {
		this.usuariocobro = usuariocobro;
	}

	public LocalDate getFechacobro() {
		return fechacobro;
	}

	public void setFechacobro(LocalDate fechacobro) {
		this.fechacobro = fechacobro;
	}

	public Long getEstado() {
		return estado;
	}

	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public Long getUsuarioanulacion() {
		return usuarioanulacion;
	}

	public void setUsuarioanulacion(Long usuarioanulacion) {
		this.usuarioanulacion = usuarioanulacion;
	}

	public LocalDate getFechaanulacion() {
		return fechaanulacion;
	}

	public void setFechaanulacion(LocalDate fechaanulacion) {
		this.fechaanulacion = fechaanulacion;
	}

	public String getRazonanulacion() {
		return razonanulacion;
	}

	public void setRazonanulacion(String razonanulacion) {
		this.razonanulacion = razonanulacion;
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

	public Long getConveniopago() {
		return conveniopago;
	}

	public void setConveniopago(Long conveniopago) {
		this.conveniopago = conveniopago;
	}

	public LocalDate getFechaconvenio() {
		return fechaconvenio;
	}

	public void setFechaconvenio(LocalDate fechaconvenio) {
		this.fechaconvenio = fechaconvenio;
	}

	public Long getEstadoconvenio() {
		return estadoconvenio;
	}

	public void setEstadoconvenio(Long estadoconvenio) {
		this.estadoconvenio = estadoconvenio;
	}

	public Long getFormapago() {
		return formapago;
	}

	public void setFormapago(Long formapago) {
		this.formapago = formapago;
	}

	public String getRefeformapago() {
		return refeformapago;
	}

	public void setRefeformapago(String refeformapago) {
		this.refeformapago = refeformapago;
	}

	public String getHoracobro() {
		return horacobro;
	}

	public void setHoracobro(String horacobro) {
		this.horacobro = horacobro;
	}

	public Long getUsuariotransferencia() {
		return usuariotransferencia;
	}

	public void setUsuariotransferencia(Long usuariotransferencia) {
		this.usuariotransferencia = usuariotransferencia;
	}

	public LocalDate getFechatransferencia() {
		return fechatransferencia;
	}

	public void setFechatransferencia(LocalDate fechatransferencia) {
		this.fechatransferencia = fechatransferencia;
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

	public LocalDate getFecmodi() {
		return fecmodi;
	}

	public void setFecmodi(LocalDate fecmodi) {
		this.fecmodi = fecmodi;
	}

	public BigDecimal getValorbase() {
		return valorbase;
	}

	public void setValorbase(BigDecimal valorbase) {
		this.valorbase = valorbase;
	}

	public Long getIdabonado() {
		return idabonado;
	}

	public void setIdabonado(Long idabonado) {
		this.idabonado = idabonado;
	}

	public BigDecimal getSwiva() {
		return swiva;
	}

	public void setSwiva(BigDecimal swiva) {
		this.swiva = swiva;
	}

	public Boolean getSwcondonar() {
		return swcondonar;
	}

	public void setSwcondonar(Boolean swcondonar) {
		this.swcondonar = swcondonar;
	}
	

}
