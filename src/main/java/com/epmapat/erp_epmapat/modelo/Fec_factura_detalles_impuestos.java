package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;

import javax.persistence.*;


@Entity
@Table(name = "fec_factura_detalles_impuestos")
public class Fec_factura_detalles_impuestos {
	@Id
	private Long idfacturadetalleimpuestos;
	private Long idfacturadetalle;
	private String codigoimpuesto;
	private String codigoporcentaje;
	private BigDecimal baseimponible;

	public Long getIdfacturadetalleimpuestos() {
		return idfacturadetalleimpuestos;
	}

	public void setIdfacturadetalleimpuestos(Long idfacturadetalleimpuestos) {
		this.idfacturadetalleimpuestos = idfacturadetalleimpuestos;
	}

	public Long getIdfacturadetalle() {
		return idfacturadetalle;
	}

	public void setIdfacturadetalle(Long idfacturadetalle) {
		this.idfacturadetalle = idfacturadetalle;
	}

	public String getCodigoimpuesto() {
		return codigoimpuesto;
	}

	public void setCodigoimpuesto(String codigoimpuesto) {
		this.codigoimpuesto = codigoimpuesto;
	}

	public String getCodigoporcentaje() {
		return codigoporcentaje;
	}

	public void setCodigoporcentaje(String codigoporcentaje) {
		this.codigoporcentaje = codigoporcentaje;
	}

	public BigDecimal getBaseimponible() {
		return baseimponible;
	}

	public void setBaseimponible(BigDecimal baseimponible) {
		this.baseimponible = baseimponible;
	}

	public Fec_factura_detalles_impuestos() {
		super();
	}

	public Fec_factura_detalles_impuestos(Long idfacturadetalleimpuestos, Long idfacturadetalle,
			String codigoimpuesto, String codigoporcentaje, BigDecimal baseimponible) {
		super();
		this.idfacturadetalleimpuestos = idfacturadetalleimpuestos;
		this.idfacturadetalle = idfacturadetalle;
		this.codigoimpuesto = codigoimpuesto;
		this.codigoporcentaje = codigoporcentaje;
		this.baseimponible = baseimponible;
	}

}
