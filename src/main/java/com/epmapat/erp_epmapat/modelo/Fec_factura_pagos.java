package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;

import javax.persistence.*;

@Entity
@Table(name = "fec_factura_pagos")
public class Fec_factura_pagos {
	@Id
	private Long idfacturapagos;
	private Long idfactura;
	private String formapago;
	private BigDecimal total;
	private Integer plazo;
	private String unidadtiempo;
	public Long getIdfacturapagos() {
		return idfacturapagos;
	}
	public void setIdfacturapagos(Long idfacturapagos) {
		this.idfacturapagos = idfacturapagos;
	}
	public Long getIdfactura() {
		return idfactura;
	}
	public void setIdfactura(Long idfactura) {
		this.idfactura = idfactura;
	}
	public String getFormapago() {
		return formapago;
	}
	public void setFormapago(String formapago) {
		this.formapago = formapago;
	}
	public BigDecimal getTotal() {
		return total;
	}
	public void setTotal(BigDecimal total) {
		this.total = total;
	}
	public Integer getPlazo() {
		return plazo;
	}
	public void setPlazo(Integer plazo) {
		this.plazo = plazo;
	}
	public String getUnidadtiempo() {
		return unidadtiempo;
	}
	public void setUnidadtiempo(String unidadtiempo) {
		this.unidadtiempo = unidadtiempo;
	}
	public Fec_factura_pagos(Long idfacturapagos, Long idfactura, String formapago, BigDecimal total, Integer plazo,
			String unidadtiempo) {
		this.idfacturapagos = idfacturapagos;
		this.idfactura = idfactura;
		this.formapago = formapago;
		this.total = total;
		this.plazo = plazo;
		this.unidadtiempo = unidadtiempo;
	}
	public Fec_factura_pagos(){
		super();
	}
}
