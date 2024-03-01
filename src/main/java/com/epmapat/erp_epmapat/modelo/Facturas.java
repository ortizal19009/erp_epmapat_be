package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

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
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE_TIME)
	@Column(name = "fechacobro")
	private Date fechacobro;
	private Long estado;
	private Long usuarioanulacion;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaanulacion")
	private Date fechaanulacion;
	private String razonanulacion;
	private Long usuarioeliminacion;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaeliminacion")
	private Date fechaeliminacion;
	private String razoneliminacion;
	private Long conveniopago;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaconvenio")
	private Date fechaconvenio;
	private Long estadoconvenio;
	private Long formapago;
	private String refeformapago;
	private String horacobro;
	private Long usuariotransferencia;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechatransferencia")
	private Date fechatransferencia;
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
	private BigDecimal valorbase;
	private Long idabonado;
	private BigDecimal interescobrado;
	@ManyToMany
	@JoinTable(name = "rubroxfac", joinColumns = @JoinColumn(name = "idfactura_facturas"), inverseJoinColumns = @JoinColumn(name = "idrubro_rubros"))
	private Set<Rubros> rubros = new HashSet<>();

	public void addRubros(Rubros rubro) {

		rubros.add(rubro);
		rubro.getFacturas().add(this);
	}

	public void removeRubros(Rubros rubro) {
		rubros.remove(rubro);
		rubro.getFacturas().remove(this);
	}

	public Facturas() {
		super();
	}
	

	public Facturas(Long idfactura, Modulos idmodulo, Clientes idcliente, String nrofactura, Long porcexoneracion,
			String razonexonera, BigDecimal totaltarifa, Integer pagado, Long usuariocobro, Date fechacobro,
			Long estado, Long usuarioanulacion, Date fechaanulacion, String razonanulacion, Long usuarioeliminacion,
			Date fechaeliminacion, String razoneliminacion, Long conveniopago, Date fechaconvenio, Long estadoconvenio,
			Long formapago, String refeformapago, String horacobro, Long usuariotransferencia, Date fechatransferencia,
			Long usucrea, Date feccrea, Long usumodi, Date fecmodi, BigDecimal valorbase, Long idabonado,
			BigDecimal interescobrado, Set<Rubros> rubros) {
		super();
		this.idfactura = idfactura;
		this.idmodulo = idmodulo;
		this.idcliente = idcliente;
		this.nrofactura = nrofactura;
		this.porcexoneracion = porcexoneracion;
		this.razonexonera = razonexonera;
		this.totaltarifa = totaltarifa;
		this.pagado = pagado;
		this.usuariocobro = usuariocobro;
		this.fechacobro = fechacobro;
		this.estado = estado;
		this.usuarioanulacion = usuarioanulacion;
		this.fechaanulacion = fechaanulacion;
		this.razonanulacion = razonanulacion;
		this.usuarioeliminacion = usuarioeliminacion;
		this.fechaeliminacion = fechaeliminacion;
		this.razoneliminacion = razoneliminacion;
		this.conveniopago = conveniopago;
		this.fechaconvenio = fechaconvenio;
		this.estadoconvenio = estadoconvenio;
		this.formapago = formapago;
		this.refeformapago = refeformapago;
		this.horacobro = horacobro;
		this.usuariotransferencia = usuariotransferencia;
		this.fechatransferencia = fechatransferencia;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.valorbase = valorbase;
		this.idabonado = idabonado;
		this.interescobrado = interescobrado;
		this.rubros = rubros;
	}

	public BigDecimal getInterescobrado() {
		return interescobrado;
	}

	public void setInterescobrado(BigDecimal interescobrado) {
		this.interescobrado = interescobrado;
	}

	public Set<Rubros> getRubros() {
		return rubros;
	}

	public void setRubros(Set<Rubros> rubros) {
		this.rubros = rubros;
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

	public Date getFechacobro() {
		return fechacobro;
	}

	public void setFechacobro(Date fechacobro) {
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

	public Date getFechaanulacion() {
		return fechaanulacion;
	}

	public void setFechaanulacion(Date fechaanulacion) {
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

	public Long getConveniopago() {
		return conveniopago;
	}

	public void setConveniopago(Long conveniopago) {
		this.conveniopago = conveniopago;
	}

	public Date getFechaconvenio() {
		return fechaconvenio;
	}

	public void setFechaconvenio(Date fechaconvenio) {
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

	public Date getFechatransferencia() {
		return fechatransferencia;
	}

	public void setFechatransferencia(Date fechatransferencia) {
		this.fechatransferencia = fechatransferencia;
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

}
