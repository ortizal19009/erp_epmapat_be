package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDate;

import javax.persistence.*;

@Entity
@Table(name ="clientes")

public class Clientes {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcliente;
	private String cedula;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtpidentifica_tpidentifica")
	private Tpidentifica idtpidentifica_tpidentifica;
	
	private String nombre;
	private String direccion; 
	private String telefono;
/* 	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name ="fechanacimiento") */
	private LocalDate fechanacimiento;
	private Long discapacitado;
	private Long porcdiscapacidad;
	private Long porcexonera;
	private Long estado;
	private String email;
	private Long usucrea;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idnacionalidad_nacionalidad")
	private Nacionalidad idnacionalidad_nacionalidad;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name ="feccrea") */
	private LocalDate feccrea;
	private Long usumodi;
/* 	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name ="fecmodi") */
	private LocalDate fecmodi;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idpjuridica_personeriajuridica")
	private PersonJuridica idpjuridica_personeriajuridica;

	public Clientes(Long idcliente, String cedula,Tpidentifica idtpidentifica_tpidentifica, String nombre, String direccion, String telefono, LocalDate fechanacimiento,
			Long discapacitado, Long porcexonera,Long porcdiscapacidad, Long estado, String email, Long usucrea,
			Nacionalidad idnacionalidad_nacionalidad, LocalDate feccrea, Long usumodi, LocalDate fecmodi,
			PersonJuridica idpjuridica_personeriajuridica) {
		super();
		this.idcliente = idcliente;
		this.cedula = cedula;
		this.idtpidentifica_tpidentifica = idtpidentifica_tpidentifica;
		this.nombre = nombre;
		this.direccion = direccion;
		this.telefono = telefono;
		this.fechanacimiento = fechanacimiento;
		this.discapacitado = discapacitado;
		this.porcdiscapacidad = porcdiscapacidad;
		this.porcexonera = porcexonera;
		this.estado = estado;
		this.email = email;
		this.usucrea = usucrea;
		this.idnacionalidad_nacionalidad = idnacionalidad_nacionalidad;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.idpjuridica_personeriajuridica = idpjuridica_personeriajuridica;
	}

	public Clientes() {
		super();
	}
	
	public Long getIdcliente() {
		return idcliente;
	}
	public void setIdcliente(Long idcliente) {
		this.idcliente = idcliente;
	}
	public String getCedula() {
		return cedula;
	}
	public void setCedula(String cedula) {
		this.cedula = cedula;
	}
	// public String getNombe() {
	// 	return nombre;
	// }
	// public void setNombe(String nombe) {
	// 	this.nombre = nombe;
	// }
	public String getDireccion() {
		return direccion;
	}
	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public LocalDate getFechanacimiento() {
		return fechanacimiento;
	}
	public void setFechanacimiento(LocalDate fechanacimiento) {
		this.fechanacimiento = fechanacimiento;
	}
	public Long getDiscapacitado() {
		return discapacitado;
	}
	public void setDiscapacitado(Long discapacitado) {
		this.discapacitado = discapacitado;
	}
	public Long getPorcexonera() {
		return porcexonera;
	}
	public void setPorcexonera(Long porcexonera) {
		this.porcexonera = porcexonera;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Long getUsucrea() {
		return usucrea;
	}
	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
	}
	public Nacionalidad getIdnacionalidad_nacionalidad() {
		return idnacionalidad_nacionalidad;
	}
	public void setIdnacionalidad_nacionalidad(Nacionalidad idnacionalidad_nacionalidad) {
		this.idnacionalidad_nacionalidad = idnacionalidad_nacionalidad;
	}
	public LocalDate getFeccrea() {
		return feccrea;
	}
	public void setFeccrea(LocalDate feccrea) {
		this.feccrea = feccrea;
	}
	public Long getUsumodi() {
		return usumodi;
	}
	public void setUsumodi(Long usumodi) {
		this.usumodi = usumodi;
	}
	public LocalDate getFecmodi() {
		return fecmodi;
	}
	public void setFecmodi(LocalDate fecmodi) {
		this.fecmodi = fecmodi;
	}
	public PersonJuridica getIdpjuridica_personeriajuridica() {
		return idpjuridica_personeriajuridica;
	}
	public void setIdpjuridica_personeriajuridica(PersonJuridica idpjuridica_personeriajuridica) {
		this.idpjuridica_personeriajuridica = idpjuridica_personeriajuridica;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getPorcdiscapacidad() {
		return porcdiscapacidad;
	}
	public void setPorcdiscapacidad(Long porcdiscapacidad) {
		this.porcdiscapacidad = porcdiscapacidad;
	}
	public Tpidentifica getIdtpidentifica_tpidentifica() {
		return idtpidentifica_tpidentifica;
	}
	public void setIdtpidentifica_tpidentifica(Tpidentifica idtpidentifica_tpidentifica) {
		this.idtpidentifica_tpidentifica = idtpidentifica_tpidentifica;
	}
}
