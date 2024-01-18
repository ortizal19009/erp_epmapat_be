package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name="rubroxfac")

public class Rubroxfac {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrubroxfac;
	private Float cantidad;
	private BigDecimal valorunitario;
	private Long estado; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idfactura_facturas")
	private Facturas idfactura_facturas;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idrubro_rubros")
	private Rubros idrubro_rubros;

	public Rubroxfac() { super();	}

	public Rubroxfac(Long idrubroxfac, Float cantidad, BigDecimal valorunitario, Long estado, Facturas idfactura_facturas,
			Rubros idrubro_rubros) {
		super();
		this.idrubroxfac = idrubroxfac;
		this.cantidad = cantidad;
		this.valorunitario = valorunitario;
		this.estado = estado;
		this.idfactura_facturas = idfactura_facturas;
		this.idrubro_rubros = idrubro_rubros;
	}

	public Long getIdrubroxfac() {
		return idrubroxfac;
	}
	public void setIdrubroxfac(Long idrubroxfac) {
		this.idrubroxfac = idrubroxfac;
	}

	public Float getCantidad() {
		return cantidad;
	}
	public void setCantidad(Float cantidad) {
		this.cantidad = cantidad;
	}

	public BigDecimal getValorunitario() {
		return valorunitario;
	}
	public void setValorunitario(BigDecimal valorunitario) {
		this.valorunitario = valorunitario;
	}

	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public Facturas getIdfactura_facturas() {
		return idfactura_facturas;
	}
	public void setIdfactura_facturas(Facturas idfactura_facturas) {
		this.idfactura_facturas = idfactura_facturas;
	}

	public Rubros getIdrubro_rubros() {
		return idrubro_rubros;
	}
	public void setIdrubro_rubros(Rubros idrubro_rubros) {
		this.idrubro_rubros = idrubro_rubros;
	}

}
