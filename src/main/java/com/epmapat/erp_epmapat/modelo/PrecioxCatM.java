package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "precioxcat")

public class PrecioxCatM {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idprecioxcat;
	private Long m3;
	private BigDecimal preciobase;
	private BigDecimal precioadicional;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcategoria_categorias")
	private Categorias idcategoria_categorias;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

	public PrecioxCatM(Long idprecioxcat, Long m3, BigDecimal preciobase, BigDecimal precioadicional,
			Categorias idcategoria_categorias, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idprecioxcat = idprecioxcat;
		this.m3 = m3;
		this.preciobase = preciobase;
		this.precioadicional = precioadicional;
		this.idcategoria_categorias = idcategoria_categorias;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public PrecioxCatM() {
		super();
	}

	public Long getIdprecioxcat() {
		return idprecioxcat;
	}
	public void setIdprecioxcat(Long idprecioxcat) {
		this.idprecioxcat = idprecioxcat;
	}
	public Long getM3() {
		return m3;
	}
	public void setM3(Long m3) {
		this.m3 = m3;
	}
	public BigDecimal getPreciobase() {
		return preciobase;
	}
	public void setPreciobase(BigDecimal preciobase) {
		this.preciobase = preciobase;
	}
	public BigDecimal getPrecioadicional() {
		return precioadicional;
	}
	public void setPrecioadicional(BigDecimal precioadicional) {
		this.precioadicional = precioadicional;
	}
	public Categorias getIdcategoria_categorias() {
		return idcategoria_categorias;
	}
	public void setIdcategoria_categorias(Categorias idcategoria_categorias) {
		this.idcategoria_categorias = idcategoria_categorias;
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
	
}
