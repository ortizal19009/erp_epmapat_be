package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "modulos")

public class Modulos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idmodulo;
	private String descripcion;
	private Long estado;
	private Long periodicidad;
	private Long conveniospago;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name ="feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name ="fecmodi")
	private Date fecmodi;

	public Modulos() {
		super();
	}
	
	public Modulos(Long idmodulo, String descripcion, Long estado, Long periodicidad, Long conveniospago, Long usucrea,
			Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idmodulo = idmodulo;
		this.descripcion = descripcion;
		this.estado = estado;
		this.periodicidad = periodicidad;
		this.conveniospago = conveniospago;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	public Long getIdmodulo() {
		return idmodulo;
	}
	public void setIdmodulo(Long idmodulo) {
		this.idmodulo = idmodulo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public Long getPeriodicidad() {
		return periodicidad;
	}
	public void setPeriodicidad(Long periodicidad) {
		this.periodicidad = periodicidad;
	}
	public Long getConveniospago() {
		return conveniospago;
	}
	public void setConveniospago(Long conveniospago) {
		this.conveniospago = conveniospago;
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
