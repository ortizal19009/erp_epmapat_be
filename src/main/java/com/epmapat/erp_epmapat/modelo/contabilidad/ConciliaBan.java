package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name="conciliaban")
public class ConciliaBan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idconcilia; 
	private Long mes; 
	private BigDecimal libinicial; 
	private BigDecimal libdebitos; 
	private BigDecimal libcreditos; 
	private BigDecimal libdepositos; 
	private BigDecimal libcheques; 
	private BigDecimal liberrores; 
	private BigDecimal baninicial; 
	private BigDecimal bancreditos; 
	private BigDecimal bandebitos; 
	private BigDecimal bancheaa; 
	private BigDecimal bannc; 
	private BigDecimal bannd; 
	private BigDecimal banerrores; 
	private String observa; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;
	public ConciliaBan() {
		super();
	
	}
	public ConciliaBan(Long idconcilia, Long mes, BigDecimal libinicial, BigDecimal libdebitos, BigDecimal libcreditos,
			BigDecimal libdepositos, BigDecimal libcheques, BigDecimal liberrores, BigDecimal baninicial,
			BigDecimal bancreditos, BigDecimal bandebitos, BigDecimal bancheaa, BigDecimal bannc, BigDecimal bannd,
			BigDecimal banerrores, String observa, Cuentas idcuenta) {
		super();
		this.idconcilia = idconcilia;
		this.mes = mes;
		this.libinicial = libinicial;
		this.libdebitos = libdebitos;
		this.libcreditos = libcreditos;
		this.libdepositos = libdepositos;
		this.libcheques = libcheques;
		this.liberrores = liberrores;
		this.baninicial = baninicial;
		this.bancreditos = bancreditos;
		this.bandebitos = bandebitos;
		this.bancheaa = bancheaa;
		this.bannc = bannc;
		this.bannd = bannd;
		this.banerrores = banerrores;
		this.observa = observa;
		this.idcuenta = idcuenta;
	}
	public Long getIdconcilia() {
		return idconcilia;
	}
	public void setIdconcilia(Long idconcilia) {
		this.idconcilia = idconcilia;
	}
	public Long getMes() {
		return mes;
	}
	public void setMes(Long mes) {
		this.mes = mes;
	}
	public BigDecimal getLibinicial() {
		return libinicial;
	}
	public void setLibinicial(BigDecimal libinicial) {
		this.libinicial = libinicial;
	}
	public BigDecimal getLibdebitos() {
		return libdebitos;
	}
	public void setLibdebitos(BigDecimal libdebitos) {
		this.libdebitos = libdebitos;
	}
	public BigDecimal getLibcreditos() {
		return libcreditos;
	}
	public void setLibcreditos(BigDecimal libcreditos) {
		this.libcreditos = libcreditos;
	}
	public BigDecimal getLibdepositos() {
		return libdepositos;
	}
	public void setLibdepositos(BigDecimal libdepositos) {
		this.libdepositos = libdepositos;
	}
	public BigDecimal getLibcheques() {
		return libcheques;
	}
	public void setLibcheques(BigDecimal libcheques) {
		this.libcheques = libcheques;
	}
	public BigDecimal getLiberrores() {
		return liberrores;
	}
	public void setLiberrores(BigDecimal liberrores) {
		this.liberrores = liberrores;
	}
	public BigDecimal getBaninicial() {
		return baninicial;
	}
	public void setBaninicial(BigDecimal baninicial) {
		this.baninicial = baninicial;
	}
	public BigDecimal getBancreditos() {
		return bancreditos;
	}
	public void setBancreditos(BigDecimal bancreditos) {
		this.bancreditos = bancreditos;
	}
	public BigDecimal getBandebitos() {
		return bandebitos;
	}
	public void setBandebitos(BigDecimal bandebitos) {
		this.bandebitos = bandebitos;
	}
	public BigDecimal getBancheaa() {
		return bancheaa;
	}
	public void setBancheaa(BigDecimal bancheaa) {
		this.bancheaa = bancheaa;
	}
	public BigDecimal getBannc() {
		return bannc;
	}
	public void setBannc(BigDecimal bannc) {
		this.bannc = bannc;
	}
	public BigDecimal getBannd() {
		return bannd;
	}
	public void setBannd(BigDecimal bannd) {
		this.bannd = bannd;
	}
	public BigDecimal getBanerrores() {
		return banerrores;
	}
	public void setBanerrores(BigDecimal banerrores) {
		this.banerrores = banerrores;
	}
	public String getObserva() {
		return observa;
	}
	public void setObserva(String observa) {
		this.observa = observa;
	}
	public Cuentas getIdcuenta() {
		return idcuenta;
	}
	public void setIdcuenta(Cuentas idcuenta) {
		this.idcuenta = idcuenta;
	} 

}
