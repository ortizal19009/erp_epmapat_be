package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "intereses")

public class Intereses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idinteres;
	private Long anio;
	private Long mes;
	private BigDecimal porcentaje; 
	private Long usucrea;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)*/
	@Column(name = "feccrea") 
	private LocalDate feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

	public Intereses() {
		super();
	}

	public Intereses(Long idinteres, Long anio, Long mes, BigDecimal porcentaje, Long usucrea, LocalDate feccrea, Long usumodi,
			Date fecmodi) {
		super();
		this.idinteres = idinteres;
		this.anio = anio;
		this.mes = mes;
		this.porcentaje = porcentaje;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public Long getIdinteres() {
		return idinteres;
	}
	
	public void setIdinteres(Long idinteres) {
		this.idinteres = idinteres;
	}
	
	public Long getAnio() {
		return anio;
	}
	
	public void setAnio(Long anio) {
		this.anio = anio;
	}
	
	public Long getMes() {
		return mes;
	}
	public void setMes(Long mes) {
		this.mes = mes;
	}
	public BigDecimal getPorcentaje() {
		return porcentaje;
	}
	public void setPorcentaje(BigDecimal porcentaje) {
		this.porcentaje = porcentaje;
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
	public Date getFecmodi() {
		return fecmodi;
	}
	public void setFecmodi(Date fecmodi) {
		this.fecmodi = fecmodi;
	}
	
}
