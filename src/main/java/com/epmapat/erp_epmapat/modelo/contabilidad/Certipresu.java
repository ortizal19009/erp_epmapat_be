package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "certipresu")

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
	private Double valor;
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
	@JoinColumn(name = "iddocumento")
	private Documentos iddocumento;

	// private Long iddocumento;

	public Certipresu() {
		super();
	}

	public Certipresu(Long idcerti, Integer tipo, Long numero, Date fecha, Double valor, String descripcion, String numdoc,
			Long usucrea, Date feccrea, Long usumodi, Date fecmodi, Beneficiarios idbene, Beneficiarios idbeneres, Documentos iddocumento) {
		super();
		this.idcerti = idcerti;
		this.tipo = tipo;
		this.numero = numero;
		this.fecha = fecha;
		this.valor = valor;
		this.descripcion = descripcion;
		this.numdoc = numdoc;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.idbene = idbene;
		this.idbeneres = idbeneres;
		this.iddocumento = iddocumento;
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
	public Double getValor() {
		return valor;
	}
	public void setValor(Double valor) {
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
	public Documentos getIddocumento() {
		return iddocumento;
	}
	public void setIddocumento(Documentos iddocumento) {
		this.iddocumento = iddocumento;
	}
	
}
