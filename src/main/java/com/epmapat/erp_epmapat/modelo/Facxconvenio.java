package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "facxconvenio")

public class Facxconvenio {
   
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfacxconvenio;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura_facturas")
	private Facturas idfactura_facturas;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idconvenio_convenios")
	private Convenios idconvenio_convenios;
	//private Long idfactura_facturas;
	//private Long idconvenio_convenios;
	
	public Facxconvenio() {
	}
	
	// public Facxconvenio(Long idfacxconvenio, Facturas idfactura_facturas, Convenios idconvenio_convenios) {
	// 	this.idfacxconvenio = idfacxconvenio;
	// 	this.idfactura_facturas = idfactura_facturas;
	// 	this.idconvenio_convenios = idconvenio_convenios;
	// }

	public Long getIdfacxconvenio() {
		return idfacxconvenio;
	}
	public void setIdfacxconvenio(Long idfacxconvenio) {
		this.idfacxconvenio = idfacxconvenio;
	}
	
	public Facturas getIdfactura_facturas() {
		return idfactura_facturas;
	}
	public void setIdfactura_facturas(Facturas idfactura_facturas) {
		this.idfactura_facturas = idfactura_facturas;
	}
	
	public Convenios getIdconvenio_convenios() {
		return idconvenio_convenios;
	}
	public void setIdconvenio_convenios(Convenios idconvenio_convenios) {
		this.idconvenio_convenios = idconvenio_convenios;
	}

}
