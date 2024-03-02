package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "asientos")
public class Asientos {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idasiento;
	private Long asiento;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecha")
	private Date fecha;
	private Integer tipasi;
	private Integer tipcom;
	private Long compro;
	private Long numcue;
	private BigDecimal totdeb;
	private BigDecimal totcre;
	private String glosa;
	private String numdoc;
	private String numdocban;
	private Boolean cerrado;
	private Boolean swretencion;
	private Long totalspi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iddocumento")
	private Documentos iddocumento;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene;
	private Long idcueban;
	private Long usucrea;

	@Column(name = "feccrea")
	private ZonedDateTime feccrea;

	// @Temporal(TemporalType.DATE)
	// @DateTimeFormat(iso = ISO.DATE)
	// @Column(name = "feccrea")
	// private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

	public Asientos() {
		super();
	
	}

	public Asientos(Long idasiento, Long asiento, Date fecha, Integer tipasi, Integer tipcom, Long compro, Long numcue,
			BigDecimal totdeb, BigDecimal totcre, String glosa, String numdoc, String numdocban, Boolean cerrado,
			Boolean swretencion, Long totalspi, Documentos iddocumento, Beneficiarios idbene, Long idcueban,
			Long usucrea, ZonedDateTime feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idasiento = idasiento;
		this.asiento = asiento;
		this.fecha = fecha;
		this.tipasi = tipasi;
		this.tipcom = tipcom;
		this.compro = compro;
		this.numcue = numcue;
		this.totdeb = totdeb;
		this.totcre = totcre;
		this.glosa = glosa;
		this.numdoc = numdoc;
		this.numdocban = numdocban;
		this.cerrado = cerrado;
		this.swretencion = swretencion;
		this.totalspi = totalspi;
		this.iddocumento = iddocumento;
		this.idbene = idbene;
		this.idcueban = idcueban;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
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

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
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

	public Boolean getCerrado() {
		return cerrado;
	}

	public void setCerrado(Boolean cerrado) {
		this.cerrado = cerrado;
	}

	public Boolean getSwretencion() {
		return swretencion;
	}

	public void setSwretencion(Boolean swretencion) {
		this.swretencion = swretencion;
	}

	public Long getTotalspi() {
		return totalspi;
	}

	public void setTotalspi(Long totalspi) {
		this.totalspi = totalspi;
	}

	public Documentos getIddocumento() {
		return iddocumento;
	}

	public void setIddocumento(Documentos iddocumento) {
		this.iddocumento = iddocumento;
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

	public ZonedDateTime getFeccrea() {
		return feccrea;
	}

	public void setFeccrea(ZonedDateTime feccrea) {
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

	public Asientos orElseThrow(Object object) {
		return null;
	}

}
