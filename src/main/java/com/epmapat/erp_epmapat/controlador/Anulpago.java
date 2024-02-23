package com.epmapat.erp_epmapat.controlador;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.Facturas;

@Entity
@Table(name = "anulpago")

public class Anulpago {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idanulpago;

	private String cedula;
	private String nombre;
	private Long idpropietarioemision;
	private String nrofactura;
	private BigDecimal totaltarifa;
	private Long idmodulo;
	private Integer usuarioanulacion;
	@Column(name = "fechaanulacion")
	private ZonedDateTime fechaanulacion;
	private String horaanulacion;
	private String razonanulacion;
	private Integer usuariocobro;
	@Column(name = "fechacobro")
	private ZonedDateTime fechacobro;
	private String horacobro;
	private BigDecimal valor;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura")
	private Facturas idfactura;
	private BigDecimal descuento;
	private BigDecimal exoneracion;
	private BigDecimal interes;

	public Long getIdanulpago() {
		return idanulpago;
	}

	public void setIdanulpago(Long idanulpago) {
		this.idanulpago = idanulpago;
	}

	public String getCedula() {
		return cedula;
	}

	public void setCedula(String cedula) {
		this.cedula = cedula;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Long getIdpropietarioemision() {
		return idpropietarioemision;
	}

	public void setIdpropietarioemision(Long idpropietarioemision) {
		this.idpropietarioemision = idpropietarioemision;
	}

	public String getNrofactura() {
		return nrofactura;
	}

	public void setNrofactura(String nrofactura) {
		this.nrofactura = nrofactura;
	}

	public BigDecimal getTotaltarifa() {
		return totaltarifa;
	}

	public void setTotaltarifa(BigDecimal totaltarifa) {
		this.totaltarifa = totaltarifa;
	}

	public Long getIdmodulo() {
		return idmodulo;
	}

	public void setIdmodulo(Long idmodulo) {
		this.idmodulo = idmodulo;
	}

	public Integer getUsuarioanulacion() {
		return usuarioanulacion;
	}

	public void setUsuarioanulacion(Integer usuarioanulacion) {
		this.usuarioanulacion = usuarioanulacion;
	}

	public ZonedDateTime getFechaanulacion() {
		return fechaanulacion;
	}

	public void setFechaanulacion(ZonedDateTime fechaanulacion) {
		this.fechaanulacion = fechaanulacion;
	}

	public String getHoraanulacion() {
		return horaanulacion;
	}

	public void setHoraanulacion(String horaanulacion) {
		this.horaanulacion = horaanulacion;
	}

	public String getRazonanulacion() {
		return razonanulacion;
	}

	public void setRazonanulacion(String razonanulacion) {
		this.razonanulacion = razonanulacion;
	}

	public Integer getUsuariocobro() {
		return usuariocobro;
	}

	public void setUsuariocobro(Integer usuariocobro) {
		this.usuariocobro = usuariocobro;
	}

	public ZonedDateTime getFechacobro() {
		return fechacobro;
	}

	public void setFechacobro(ZonedDateTime fechacobro) {
		this.fechacobro = fechacobro;
	}

	public String getHoracobro() {
		return horacobro;
	}

	public void setHoracobro(String horacobro) {
		this.horacobro = horacobro;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Facturas getIdfactura() {
		return idfactura;
	}

	public void setIdfactura(Facturas idfactura) {
		this.idfactura = idfactura;
	}

	public BigDecimal getDescuento() {
		return descuento;
	}

	public void setDescuento(BigDecimal descuento) {
		this.descuento = descuento;
	}

	public BigDecimal getExoneracion() {
		return exoneracion;
	}

	public void setExoneracion(BigDecimal exoneracion) {
		this.exoneracion = exoneracion;
	}

	public BigDecimal getInteres() {
		return interes;
	}

	public void setInteres(BigDecimal interes) {
		this.interes = interes;
	}

}
