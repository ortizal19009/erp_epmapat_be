package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "transaci")
public class Transaci {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long inttra;
	private Integer orden;
	private String codcue;
	private BigDecimal valor;
	private Integer debcre;
	private String descri;
	private String numdoc;
	private Integer tiptran;
	private Long totbene;
	private Integer swconcili;
	private Integer mesconcili;

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
	@JoinColumn(name = "intdoc")
	private Documentos intdoc;

	private Long intpre;
	private String codpartr;
	private String codcueiog;
	private BigDecimal debeiog;
	private BigDecimal haberiog;
	private Long asierefe;

	private Long usucrea;
	//@Column(name = "feccrea")
	private LocalDate feccrea;

	private Long usumodi;
	// @Column(name = "fecmodi")
	private LocalDate fecmodi;

	
	public Transaci() {
		super();
		// TODO Auto-generated constructor stub
	}

	public Long getInttra() {
		return inttra;
	}

	public void setInttra(Long inttra) {
		this.inttra = inttra;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
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

	public Integer getDebcre() {
		return debcre;
	}

	public void setDebcre(Integer debcre) {
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

	public Integer getSwconcili() {
		return swconcili;
	}

	public void setSwconcili(Integer swconcili) {
		this.swconcili = swconcili;
	}

	public Integer getMesconcili() {
		return mesconcili;
	}

	public void setMesconcili(Integer mesconcili) {
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

	public Documentos getIntdoc() {
		return intdoc;
	}

	public void setIntdoc(Documentos intdoc) {
		this.intdoc = intdoc;
	}

	public Long getIntpre() {
		return intpre;
	}

	public void setIntpre(Long intpre) {
		this.intpre = intpre;
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

}
