package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "suspensiones")

public class SuspensionesM {

	@Id
	private Long idsuspension;
	private Long tipo; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idsuspension_origen")
	private SuspensionesM idsuspension_origen;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecha")
	private Date fecha;
	private Long numero;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iddocumento_documentos")
	private Documentos iddocumento_documentos;
	private String numdoc; 
	private String observa; 
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
	private Long total;

	public SuspensionesM() {
		super();
		
	}
	public SuspensionesM(Long idsuspension, Long tipo, SuspensionesM idsuspension_origen, Date fecha, Long numero, Documentos iddocumento_documentos,
			String numdoc, String observa, Long usucrea, Date feccrea, Long usumodi, Date fecmodi, Long total) {
		super();
		this.idsuspension = idsuspension;
		this.tipo = tipo;
		this.idsuspension_origen = idsuspension_origen;
		this.fecha = fecha;
		this.numero = numero;
		this.iddocumento_documentos = iddocumento_documentos;
		this.numdoc = numdoc;
		this.observa = observa;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.total = total;
	}

	public Long getIdsuspension() {
		return idsuspension;
	}
	public void setIdsuspension(Long idsuspension) {
		this.idsuspension = idsuspension;
	}
	public Long getTipo() {
		return tipo;
	}
	public SuspensionesM getIdsuspension_origen() {
		return idsuspension_origen;
	}
	public void setIdsuspension_origen(SuspensionesM idsuspension_origen) {
		this.idsuspension_origen = idsuspension_origen;
	}
	public Long getTotal() {
		return total;
	}
	public void setTotal(Long total) {
		this.total = total;
	}
	public void setTipo(Long tipo) {
		this.tipo = tipo;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	public Documentos getIddocumento_documentos() {
		return iddocumento_documentos;
	}
	public void setIddocumento_documentos(Documentos iddocumento_documentos) {
		this.iddocumento_documentos = iddocumento_documentos;
	}
	public String getNumdoc() {
		return numdoc;
	}
	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}
	public String getObserva() {
		return observa;
	}
	public void setObserva(String observa) {
		this.observa = observa;
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
