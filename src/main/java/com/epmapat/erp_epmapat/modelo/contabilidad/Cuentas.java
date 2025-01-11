package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name = "cuentas")
public class Cuentas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcuenta;
	private String codcue;
	private String nomcue;
	private String grucue;
	private Integer nivcue;
	private Integer movcue;
	private String asodebe;
	private String asohaber;
	private BigDecimal debito;
	private BigDecimal credito;
	private BigDecimal saldo;
	private BigDecimal balance;
	private Long intgrupo;
	private Integer sigef;
	private Integer tiptran;
	private Long usucrea;
	private LocalDate feccrea;

	private Long usumodi;
	private LocalDate fecmodi;
	private Long grufluefec;
	private Long resulcostos;
	private Long balancostos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idnivel")
	private Niveles idnivel;

	public Cuentas() {
		super();
	}

	public Integer getMovcue() {
		return movcue;
	}

	public void setMovcue(Integer movcue) {
		this.movcue = movcue;
	}

	public Integer getSigef() {
		return sigef;
	}

	public void setSigef(Integer sigef) {
		this.sigef = sigef;
	}

	public LocalDate getFeccrea() {
		return feccrea;
	}

	public void setFeccrea(LocalDate feccrea) {
		this.feccrea = feccrea;
	}

	public LocalDate getFecmodi() {
		return fecmodi;
	}

	public void setFecmodi(LocalDate fecmodi) {
		this.fecmodi = fecmodi;
	}

	public Integer getNivcue() {
		return nivcue;
	}

	public void setNivcue(Integer nivcue) {
		this.nivcue = nivcue;
	}

	public Long getIdcuenta() {
		return idcuenta;
	}

	public void setIdcuenta(Long idcuenta) {
		this.idcuenta = idcuenta;
	}

	public String getCodcue() {
		return codcue;
	}

	public void setCodcue(String codcue) {
		this.codcue = codcue;
	}

	public String getNomcue() {
		return nomcue;
	}

	public void setNomcue(String nomcue) {
		this.nomcue = nomcue;
	}

	public String getGrucue() {
		return grucue;
	}

	public void setGrucue(String grucue) {
		this.grucue = grucue;
	}

	public Niveles getIdnivel() {
		return idnivel;
	}

	public void setIdnivel(Niveles idnivel) {
		this.idnivel = idnivel;
	}

	public String getAsodebe() {
		return asodebe;
	}

	public void setAsodebe(String asodebe) {
		this.asodebe = asodebe;
	}

	public String getAsohaber() {
		return asohaber;
	}

	public void setAsohaber(String asohaber) {
		this.asohaber = asohaber;
	}

	public BigDecimal getDebito() {
		return debito;
	}

	public void setDebito(BigDecimal debito) {
		this.debito = debito;
	}

	public BigDecimal getCredito() {
		return credito;
	}

	public void setCredito(BigDecimal credito) {
		this.credito = credito;
	}

	public BigDecimal getSaldo() {
		return saldo;
	}

	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}

	public Long getIntgrupo() {
		return intgrupo;
	}

	public void setIntgrupo(Long intgrupo) {
		this.intgrupo = intgrupo;
	}

	// public Boolean getSigef() {
	// return sigef;
	// }
	// public void setSigef(Boolean sigef) {
	// this.sigef = sigef;
	// }
	public Integer getTiptran() {
		return tiptran;
	}

	public void setTiptran(Integer tiptran) {
		this.tiptran = tiptran;
	}

	public Long getUsucrea() {
		return usucrea;
	}

	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}

	// public ZonedDateTime getFeccrea() {
	// return feccrea;
	// }
	// public void setFeccrea(ZonedDateTime feccrea) {
	// this.feccrea = feccrea;
	// }
	public Long getUsumodi() {
		return usumodi;
	}

	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}

	// public Date getFecmodi() {
	// return fecmodi;
	// }
	// public void setFecmodi(Date fecmodi) {
	// this.fecmodi = fecmodi;
	// }
	public Long getGrufluefec() {
		return grufluefec;
	}

	public void setGrufluefec(Long grufluefec) {
		this.grufluefec = grufluefec;
	}

	public Long getResulcostos() {
		return resulcostos;
	}

	public void setResulcostos(Long resulcostos) {
		this.resulcostos = resulcostos;
	}

	public Long getBalancostos() {
		return balancostos;
	}

	public void setBalancostos(Long balancostos) {
		this.balancostos = balancostos;
	}

}
