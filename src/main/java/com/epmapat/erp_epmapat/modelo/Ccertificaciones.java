package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "ccertificaciones")

public class Ccertificaciones {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcertificacion;
	private Long numero;
	private Long anio;
	private String referenciadocumento;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtpcertifica_tpcertifica")
	private TpCertifica idtpcertifica_tpcertifica;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura_facturas")
	private Facturas idfactura_facturas;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes; 
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name ="fecmodi")
	private Date fecmodi;

	public Ccertificaciones() {
		super();
	}
	
	public Ccertificaciones(Long idcertificacion, Long numero, Long anio, String referenciadocumento,
			TpCertifica idtpcertifica_tpcertifica, Facturas idfactura_facturas, Clientes idcliente_clientes, Long usucrea, Date feccrea,
			Long usumodi, Date fecmodi) {
		super();
		this.idcertificacion = idcertificacion;
		this.numero = numero;
		this.anio = anio;
		this.referenciadocumento = referenciadocumento;
		this.idtpcertifica_tpcertifica = idtpcertifica_tpcertifica;
		this.idfactura_facturas = idfactura_facturas;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public Long getIdcertificacion() {
		return idcertificacion;
	}
	public void setIdcertificacion(Long idcertificacion) {
		this.idcertificacion = idcertificacion;
	}
	public Long getNumero() {
		return numero;
	}
	public void setNumero(Long numero) {
		this.numero = numero;
	}
	public Long getAnio() {
		return anio;
	}
	public void setAnio(Long anio) {
		this.anio = anio;
	}
	public String getReferenciadocumento() {
		return referenciadocumento;
	}
	public void setReferenciadocumento(String referenciadocumento) {
		this.referenciadocumento = referenciadocumento;
	}
	public TpCertifica getIdtpcertifica_tpcertifica() {
		return idtpcertifica_tpcertifica;
	}
	public void setIdtpcertifica_tpcertifica(TpCertifica idtpcertifica_tpcertifica) {
		this.idtpcertifica_tpcertifica = idtpcertifica_tpcertifica;
	}

	public Facturas getIdfactura_facturas() {
		return idfactura_facturas;
	}
	public void setIdfactura_facturas(Facturas idfactura_facturas) {
		this.idfactura_facturas = idfactura_facturas;
	}

	public Clientes getIdcliente_clientes() {
		return idcliente_clientes;
	}
	public void setIdcliente_clientes(Clientes idcliente_clientes) {
		this.idcliente_clientes = idcliente_clientes;
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
