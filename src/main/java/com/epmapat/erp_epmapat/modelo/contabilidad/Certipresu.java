package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "certificaciones")

public class Certipresu {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcerti;
	private Integer tipo;
	private Long numero;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecha")
	private Date fecha;
	private BigDecimal valor;
	private String descripcion;
	private String numdoc;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbeneres")
	private Beneficiarios idbeneres;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc;

	public Certipresu() {
		super();
	}

	public Long getIdcerti() {
		return idcerti;
	}
	public void setIdcerti(Long idcerti) {
		this.idcerti = idcerti;
	}
	public Integer getTipo() {
		return tipo;
	}
	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	public Date getFecha() {
		return fecha;
	}
	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getNumdoc() {
		return numdoc;
	}
	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
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
	public Beneficiarios getIdbene() {
		return idbene;
	}
	public void setIdbene(Beneficiarios idbene) {
		this.idbene = idbene;
	}
	public Beneficiarios getIdbeneres() {
		return idbeneres;
	}
	public void setIdbeneres(Beneficiarios idbeneres) {
		this.idbeneres = idbeneres;
	}
	public Documentos getIntdoc() {
		return intdoc;
	}
	public void setIntdoc(Documentos intdoc) {
		this.intdoc = intdoc;
	}
	
}
