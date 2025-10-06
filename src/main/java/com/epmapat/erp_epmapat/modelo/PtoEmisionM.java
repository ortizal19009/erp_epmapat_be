package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "ptoemision")

public class PtoEmisionM {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idptoemision;
	private String establecimiento;
	private String direccion;
	private Long estado;
	private String telefono;
	private String nroautorizacion;
	private Long usucrea;	
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;

	public PtoEmisionM() {
		super();
	}

	public PtoEmisionM(Long idptoemision, String establecimiento, String direccion, Long estado, String telefono, String nroautorizacion,
			Long usucrea, Date feccrea) {
		super();
		this.idptoemision = idptoemision;
		this.establecimiento = establecimiento;
		this.direccion = direccion;
		this.estado = estado;
		this.telefono = telefono;
		this.nroautorizacion = nroautorizacion;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
	}

	public Long getIdptoemision() {
		return idptoemision;
	}
	public void setIdptoemision(Long idptoemision) {
		this.idptoemision = idptoemision;
	}
	
	public String getEstablecimiento() {
		return establecimiento;
	}
	public void setEstablecimiento(String establecimiento) {
		this.establecimiento = establecimiento;
	}

	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getNroautorizacion() {
		return nroautorizacion;
	}
	public void setNroautorizacion(String nroautorizacion) {
		this.nroautorizacion = nroautorizacion;
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

}
