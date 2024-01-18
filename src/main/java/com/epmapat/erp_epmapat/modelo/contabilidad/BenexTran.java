package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

// public class BenexTran {
   
// }

@Entity
@Table(name = "benextran")
public class BenexTran {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idbenxtra; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtransa")
	private Transaci idtransa; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbene")
	private Beneficiarios idbene; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "iddocumento")
	private Documentos iddocumento; 
	private String numdoc; 
	private BigDecimal valor; 
	private BigDecimal totpagcob; 
	private BigDecimal pagocobro; 
	private Long idpagcob; 
	private Long intpre; 
	private String codparreci; 
	private String codcuereci; 
	private Long asierefe;
	public BenexTran() {
		super();
		
	}
	public BenexTran(Long idbenxtra, Transaci idtransa, Beneficiarios idbene, Documentos iddocumento, String numdoc,
			BigDecimal valor, BigDecimal totpagcob, BigDecimal pagocobro, Long idpagcob, Long intpre, String codparreci,
			String codcuereci, Long asierefe) {
		super();
		this.idbenxtra = idbenxtra;
		this.idtransa = idtransa;
		this.idbene = idbene;
		this.iddocumento = iddocumento;
		this.numdoc = numdoc;
		this.valor = valor;
		this.totpagcob = totpagcob;
		this.pagocobro = pagocobro;
		this.idpagcob = idpagcob;
		this.intpre = intpre;
		this.codparreci = codparreci;
		this.codcuereci = codcuereci;
		this.asierefe = asierefe;
	}
	public Long getIdbenxtra() {
		return idbenxtra;
	}
	public void setIdbenxtra(Long idbenxtra) {
		this.idbenxtra = idbenxtra;
	}
	public Transaci getIdtransa() {
		return idtransa;
	}
	public void setIdtransa(Transaci idtransa) {
		this.idtransa = idtransa;
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
	public String getNumdoc() {
		return numdoc;
	}
	public void setNumdoc(String numdoc) {
		this.numdoc = numdoc;
	}
	public BigDecimal getValor() {
		return valor;
	}
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	public BigDecimal getTotpagcob() {
		return totpagcob;
	}
	public void setTotpagcob(BigDecimal totpagcob) {
		this.totpagcob = totpagcob;
	}
	public BigDecimal getPagocobro() {
		return pagocobro;
	}
	public void setPagocobro(BigDecimal pagocobro) {
		this.pagocobro = pagocobro;
	}
	public Long getIdpagcob() {
		return idpagcob;
	}
	public void setIdpagcob(Long idpagcob) {
		this.idpagcob = idpagcob;
	}
	public Long getIntpre() {
		return intpre;
	}
	public void setIntpre(Long intpre) {
		this.intpre = intpre;
	}
	public String getCodparreci() {
		return codparreci;
	}
	public void setCodparreci(String codparreci) {
		this.codparreci = codparreci;
	}
	public String getCodcuereci() {
		return codcuereci;
	}
	public void setCodcuereci(String codcuereci) {
		this.codcuereci = codcuereci;
	}
	public Long getAsierefe() {
		return asierefe;
	}
	public void setAsierefe(Long asierefe) {
		this.asierefe = asierefe;
	}

}
