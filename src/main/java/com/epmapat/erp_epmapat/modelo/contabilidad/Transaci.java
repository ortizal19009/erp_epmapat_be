package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "transaci")
public class Transaci {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtransa;
	private Long orden;
	private String codcue;
	private BigDecimal valor;
	private Long debcre;
	private String descri;
	private String numdoc;
	private Integer tiptran;
	private Long totbene;
	private Boolean swconcili;
	private Long mesconcili;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idasiento")
	private Asientos idasiento;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iddocumento")
	private Documentos iddocumento;
	private Long idpresupue;
	private String codpartr;
	private String codcueiog;
	private BigDecimal debeiog;
	private BigDecimal haberiog;
	private Long asierefe;

	private Long usucrea;
	@Column(name = "feccrea")
	private ZonedDateTime feccrea;

	private Long usumodi;
	@Column(name = "fecmodi")
	private ZonedDateTime fecmodi;

	public Transaci() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Transaci(Long idtransa, Long orden, String codcue, BigDecimal valor, Long debcre, String descri,
			String numdoc, Integer tiptran, Long totbene, Boolean swconcili, Long mesconcili, Asientos idasiento,
			Cuentas idcuenta, Beneficiarios idbene, Documentos iddocumento, Long idpresupue, String codpartr,
			String codcueiog, BigDecimal debeiog, BigDecimal haberiog, Long asierefe, Long usucrea, ZonedDateTime feccrea,
			Long usumodi, ZonedDateTime fecmodi) {
		super();
		this.idtransa = idtransa;
		this.orden = orden;
		this.codcue = codcue;
		this.valor = valor;
		this.debcre = debcre;
		this.descri = descri;
		this.numdoc = numdoc;
		this.tiptran = tiptran;
		this.totbene = totbene;
		this.swconcili = swconcili;
		this.mesconcili = mesconcili;
		this.idasiento = idasiento;
		this.idcuenta = idcuenta;
		this.idbene = idbene;
		this.iddocumento = iddocumento;
		this.idpresupue = idpresupue;
		this.codpartr = codpartr;
		this.codcueiog = codcueiog;
		this.debeiog = debeiog;
		this.haberiog = haberiog;
		this.asierefe = asierefe;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public Long getIdtransa() {
		return idtransa;
	}

	public void setIdtransa(Long idtransa) {
		this.idtransa = idtransa;
	}

	public Long getOrden() {
		return orden;
	}

	public void setOrden(Long orden) {
		this.orden = orden;
	}

	public String getCodcue() {
		return codcue;
	}

	public void setCodcue(String codcue) {
		this.codcue = codcue;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Long getDebcre() {
		return debcre;
	}

	public void setDebcre(Long debcre) {
		this.debcre = debcre;
	}

	public String getDescri() {
		return descri;
	}

	public void setDescri(String descri) {
		this.descri = descri;
	}

	public String getNumdoc() {
		return numdoc;
	}

	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}

	public Integer getTiptran() {
		return tiptran;
	}

	public void setTiptran(Integer tiptran) {
		this.tiptran = tiptran;
	}

	public Long getTotbene() {
		return totbene;
	}

	public void setTotbene(Long totbene) {
		this.totbene = totbene;
	}

	public Boolean getSwconcili() {
		return swconcili;
	}

	public void setSwconcili(Boolean swconcili) {
		this.swconcili = swconcili;
	}

	public Long getMesconcili() {
		return mesconcili;
	}

	public void setMesconcili(Long mesconcili) {
		this.mesconcili = mesconcili;
	}

	public Asientos getIdasiento() {
		return idasiento;
	}

	public void setIdasiento(Asientos idasiento) {
		this.idasiento = idasiento;
	}

	public Cuentas getIdcuenta() {
		return idcuenta;
	}

	public void setIdcuenta(Cuentas idcuenta) {
		this.idcuenta = idcuenta;
	}

	public Beneficiarios getIdbene() {
		return idbene;
	}

	public void setIdbene(Beneficiarios idbene) {
		this.idbene = idbene;
	}

	public Documentos getIddocumento() {
		return iddocumento;
	}

	public void setIddocumento(Documentos iddocumento) {
		this.iddocumento = iddocumento;
	}

	public Long getIdpresupue() {
		return idpresupue;
	}

	public void setIdpresupue(Long idpresupue) {
		this.idpresupue = idpresupue;
	}

	public String getCodpartr() {
		return codpartr;
	}

	public void setCodpartr(String codpartr) {
		this.codpartr = codpartr;
	}

	public String getCodcueiog() {
		return codcueiog;
	}

	public void setCodcueiog(String codcueiog) {
		this.codcueiog = codcueiog;
	}

	public BigDecimal getDebeiog() {
		return debeiog;
	}

	public void setDebeiog(BigDecimal debeiog) {
		this.debeiog = debeiog;
	}

	public BigDecimal getHaberiog() {
		return haberiog;
	}

	public void setHaberiog(BigDecimal haberiog) {
		this.haberiog = haberiog;
	}

	public Long getAsierefe() {
		return asierefe;
	}

	public void setAsierefe(Long asierefe) {
		this.asierefe = asierefe;
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

	public ZonedDateTime getFecmodi() {
		return fecmodi;
	}

	public void setFecmodi(ZonedDateTime fecmodi) {
		this.fecmodi = fecmodi;
	}

}
