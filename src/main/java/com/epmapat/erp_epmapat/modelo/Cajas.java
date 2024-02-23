package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

@Entity
@Table(name= "cajas")

public class Cajas {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcaja;
	private String descripcion;
	private String codigo;
	private Long estado;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idptoemision_ptoemision")
	private PtoEmisionM idptoemision_ptoemision;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso= ISO.DATE)
	@Column(name="feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name="fecmodi")
	private Date fecmodi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idusuario_usuarios")
	private Usuarios idusuario_usuarios; 
	private String ultimafact; 
	
	public String getUltimafact() {
		return ultimafact;
	}
	public void setUltimafact(String ultimafact) {
		this.ultimafact = ultimafact;
	}
	public Long getIdcaja() {
		return idcaja;
	}
	public void setIdcaja(Long idcaja) {
		this.idcaja = idcaja;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
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
	public Cajas() {
		super();
		
	}
	public PtoEmisionM getIdptoemision_ptoemision() {
		return idptoemision_ptoemision;
	}
	public void setIdptoemision_ptoemision(PtoEmisionM idptoemision_ptoemision) {
		this.idptoemision_ptoemision = idptoemision_ptoemision;
	}
	public Usuarios getIdusuario_usuarios() {
		return idusuario_usuarios;
	}
	public void setIdusuario_usuarios(Usuarios idusuario_usuarios) {
		this.idusuario_usuarios = idusuario_usuarios;
	}
	
	public Cajas(Long idcaja, String descripcion, String codigo, Long estado, PtoEmisionM idptoemision_ptoemision,
			Long usucrea, Date feccrea, Long usumodi, Date fecmodi, Usuarios idusuario_usuarios, String ultimafact) {
		super();
		this.idcaja = idcaja;
		this.descripcion = descripcion;
		this.codigo = codigo;
		this.estado = estado;
		this.idptoemision_ptoemision = idptoemision_ptoemision;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.idusuario_usuarios = idusuario_usuarios;
		this.ultimafact = ultimafact; 
	}

}
