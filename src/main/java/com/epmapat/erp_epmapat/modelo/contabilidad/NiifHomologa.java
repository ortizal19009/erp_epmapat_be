package com.epmapat.erp_epmapat.modelo.contabilidad;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "niifhomologa")

public class NiifHomologa {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY )
	private Long idhomologa;
	private String codcueniif;
	private String codcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idniifcue")
	private NiifCuentas idniifcue;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;

	public NiifHomologa() {
		super();
	
	}

	public NiifHomologa(Long idhomologa, String codcueniif, String codcue, NiifCuentas idniifcue, Cuentas idcuenta) {
		super();
		this.idhomologa = idhomologa;
		this.codcueniif = codcueniif;
		this.codcue = codcue;
		this.idniifcue = idniifcue;
		this.idcuenta = idcuenta;
	}

	public Long getIdhomologa() {
		return idhomologa;
	}

	public void setIdhomologa(Long idhomologa) {
		this.idhomologa = idhomologa;
	}

	public String getCodcueniif() {
		return codcueniif;
	}

	public void setCodcueniif(String codcueniif) {
		this.codcueniif = codcueniif;
	}

	public String getCodcue() {
		return codcue;
	}

	public void setCodcue(String codcue) {
		this.codcue = codcue;
	}

	public NiifCuentas getIdniifcue() {
		return idniifcue;
	}

	public void setIdniifcue(NiifCuentas idniifcue) {
		this.idniifcue = idniifcue;
	}

	public Cuentas getIdcuenta() {
		return idcuenta;
	}

	public void setIdcuenta(Cuentas idcuenta) {
		this.idcuenta = idcuenta;
	}

}
