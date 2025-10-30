package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "asientos")
public class Asientos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idasiento;
	private Long asiento;
	private LocalDate fecha;
	private Integer tipasi;
	private Integer tipcom;
	private Long compro;
	private Long numcue;
	private BigDecimal totdeb;
	private BigDecimal totcre;
	private String glosa;
	private String numdoc;
	private String numdocban;
	private Integer cerrado;
	private Integer swretencion;
	private Long totalspi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene;
	private Long idcueban;
	private Long usucrea;

	private LocalDate feccrea;

	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "feccrea")
	// private Date feccrea;
	private Long usumodi;
	private LocalDate fecmodi;

	public Asientos() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getIdasiento() {
		return idasiento;
	}

	public void setIdasiento(Long idasiento) {
		this.idasiento = idasiento;
	}

	public Long getAsiento() {
		return asiento;
	}

	public void setAsiento(Long asiento) {
		this.asiento = asiento;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

	public Integer getTipasi() {
		return tipasi;
	}

	public void setTipasi(Integer tipasi) {
		this.tipasi = tipasi;
	}

	public Integer getTipcom() {
		return tipcom;
	}

	public void setTipcom(Integer tipcom) {
		this.tipcom = tipcom;
	}

	public Long getCompro() {
		return compro;
	}

	public void setCompro(Long compro) {
		this.compro = compro;
	}

	public Long getNumcue() {
		return numcue;
	}

	public void setNumcue(Long numcue) {
		this.numcue = numcue;
	}

	public BigDecimal getTotdeb() {
		return totdeb;
	}

	public void setTotdeb(BigDecimal totdeb) {
		this.totdeb = totdeb;
	}

	public BigDecimal getTotcre() {
		return totcre;
	}

	public void setTotcre(BigDecimal totcre) {
		this.totcre = totcre;
	}

	public String getGlosa() {
		return glosa;
	}

	public void setGlosa(String glosa) {
		this.glosa = glosa;
	}

	public String getNumdoc() {
		return numdoc;
	}

	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}

	public String getNumdocban() {
		return numdocban;
	}

	public void setNumdocban(String numdocban) {
		this.numdocban = numdocban;
	}

	public Integer getCerrado() {
		return cerrado;
	}

	public void setCerrado(Integer cerrado) {
		this.cerrado = cerrado;
	}

	public Integer getSwretencion() {
		return swretencion;
	}

	public void setSwretencion(Integer swretencion) {
		this.swretencion = swretencion;
	}

	public Long getTotalspi() {
		return totalspi;
	}

	public void setTotalspi(Long totalspi) {
		this.totalspi = totalspi;
	}

	public Documentos getIntdoc() {
		return intdoc;
	}

	public void setIntdoc(Documentos intdoc) {
		this.intdoc = intdoc;
	}

	public Beneficiarios getIdbene() {
		return idbene;
	}

	public void setIdbene(Beneficiarios idbene) {
		this.idbene = idbene;
	}

	public Long getIdcueban() {
		return idcueban;
	}

	public void setIdcueban(Long idcueban) {
		this.idcueban = idcueban;
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

	public Asientos orElseThrow(Object object) {
		return null;
	}

}
