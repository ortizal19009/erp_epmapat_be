package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name="ctramites")

public class CtramitesM {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtramite;
	private Long estado;
	private Float total;
	private String descripcion;
	private Long cuotas;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "validohasta")
	private Date validohasta; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idtptramite_tptramite")
	private TpTramiteM idtptramite_tptramite;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idcliente_clientes")
	private Clientes idcliente_clientes;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
	private Long usumodi; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

	public CtramitesM() {
		super();
	}

	public CtramitesM(Long idtramite, Long estado, Float total, String descripcion, Long cuotas, Date validohasta,
			TpTramiteM idtptramite_tptramite, Clientes idcliente_clientes, Long usucrea, Date feccrea, Long usumodi,
			Date fecmodi) {
		super();
		this.idtramite = idtramite;
		this.estado = estado;
		this.total = total;
		this.descripcion = descripcion;
		this.cuotas = cuotas;
		this.validohasta = validohasta;
		this.idtptramite_tptramite = idtptramite_tptramite;
		this.idcliente_clientes = idcliente_clientes;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	
	public Long getIdtramite() {
		return idtramite;
	}
	public void setIdtramite(Long idtramite) {
		this.idtramite = idtramite;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public Float getTotal() {
		return total;
	}
	public void setTotal(Float total) {
		this.total = total;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getCuotas() {
		return cuotas;
	}
	public void setCuotas(Long cuotas) {
		this.cuotas = cuotas;
	}
	public Date getValidohasta() {
		return validohasta;
	}
	public void setValidohasta(Date validohasta) {
		this.validohasta = validohasta;
	}
	public TpTramiteM getIdtptramite_tptramite() {
		return idtptramite_tptramite;
	}
	public void setIdtptramite_tptramite(TpTramiteM idtptramite_tptramite) {
		this.idtptramite_tptramite = idtptramite_tptramite;
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
