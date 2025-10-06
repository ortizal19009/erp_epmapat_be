package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;


import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name="tpreclamo")

public class TpReclamoM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtpreclamo;
	private String descripcion;
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

	public TpReclamoM() {
		super();
	}
	
	public TpReclamoM(Long idtpreclamo, String descripcion, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idtpreclamo = idtpreclamo;
		this.descripcion = descripcion;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	public Long getIdtpreclamo() {
		return idtpreclamo;
	}
	public void setIdtpreclamo(Long idtpreclamo) {
		this.idtpreclamo = idtpreclamo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
