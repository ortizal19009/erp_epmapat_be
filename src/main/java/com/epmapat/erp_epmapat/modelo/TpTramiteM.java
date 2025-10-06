package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "tptramite")

public class TpTramiteM {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtptramite; 
	private String descripcion; 
	private Long estado; 
	private Long tipocalculo; 
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

	public TpTramiteM() {
		super();
	}

	public TpTramiteM(Long idtptramite, String descripcion, Long estado, Long tipocalculo, Long usucrea, Date feccrea,
			Long usumodi, Date fecmodi) {
		super();
		this.idtptramite = idtptramite;
		this.descripcion = descripcion;
		this.estado = estado;
		this.tipocalculo = tipocalculo;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	
	public Long getIdtptramite() {
		return idtptramite;
	}
	public void setIdtptramite(Long idtptramite) {
		this.idtptramite = idtptramite;
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
	public Long getTipocalculo() {
		return tipocalculo;
	}
	public void setTipocalculo(Long tipocalculo) {
		this.tipocalculo = tipocalculo;
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
