package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "cuentas")
public class Cuentas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcuenta;
	private String codcue;
	private String nomcue;
	private String grucue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idnivel")
	private Niveles idnivel;
	private Boolean movcue;
	private String asodebe;
	private String asohaber;
	private BigDecimal debito;
	private BigDecimal credito;
	private BigDecimal saldo;
	private BigDecimal balance;
	private Long intgrupo;
	private Boolean sigef;
	private Integer tiptran;
	private Long usucrea;

	@Column(name = "feccrea")
	private ZonedDateTime feccrea;
	
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;
	private Long grufluefec;
	private Long resulcostos;
	private Long balancostos;

	public Cuentas() {
		super();
		// TODO Auto-generated constructor stub
	}
	public Cuentas(Long idcuenta, String codcue, String nomcue, String grucue, Niveles idnivel, Boolean movcue,
			String asodebe, String asohaber, BigDecimal debito, BigDecimal credito, BigDecimal saldo,
			BigDecimal balance, BigDecimal b1, BigDecimal b2, BigDecimal b3, BigDecimal b4, BigDecimal b5,
			BigDecimal b6, BigDecimal b7, BigDecimal b8, Long intgrupo, Boolean sigef, Integer tiptran, Long usucrea,
			ZonedDateTime feccrea, Long usumodi, Date fecmodi, Long grufluefec, Long resulcostos, Long balancostos) {
		super();
		this.idcuenta = idcuenta;
		this.codcue = codcue;
		this.nomcue = nomcue;
		this.grucue = grucue;
		this.idnivel = idnivel;
		this.movcue = movcue;
		this.asodebe = asodebe;
		this.asohaber = asohaber;
		this.debito = debito;
		this.credito = credito;
		this.saldo = saldo;
		this.balance = balance;
		this.intgrupo = intgrupo;
		this.sigef = sigef;
		this.tiptran = tiptran;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.grufluefec = grufluefec;
		this.resulcostos = resulcostos;
		this.balancostos = balancostos;
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
	public Boolean getMovcue() {
		return movcue;
	}
	public void setMovcue(Boolean movcue) {
		this.movcue = movcue;
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
	public Boolean getSigef() {
		return sigef;
	}
	public void setSigef(Boolean sigef) {
		this.sigef = sigef;
	}
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
