package com.epmapat.erp_epmapat.modelo;

import java.time.LocalDate;

import javax.persistence.*;


@Entity
@Table(name = "abonados")

public class Abonados {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idabonado;
	private String nromedidor;
	private Long lecturainicial;
	private Long estado;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechainstalacion") */
	private LocalDate fechainstalacion;
	private String marca;
	private Long secuencia;
	private String direccionubicacion;
	private String localizacion;
	private String observacion;
	private String departamento;
	private String piso;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idresponsable")
	private Clientes idresponsable;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcategoria_categorias")
	private Categorias idcategoria_categorias;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idruta_rutas")
	private Rutas idruta_rutas;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes;
	@ManyToOne
	@JoinColumn(name = "idubicacionm_ubicacionm")
	private Ubicacionm idubicacionm_ubicacionm;
	@ManyToOne
	@JoinColumn(name = "idtipopago_tipopago")
	private Tipopago idtipopago_tipopago;
	@ManyToOne
	@JoinColumn(name = "idestadom_estadom")
	private Estadom idestadom_estadom;
	private Long medidorprincipal;
	/*
	 * @ManyToMany
	 * 
	 * @JoinTable(name = "servxabo", joinColumns = @JoinColumn(name =
	 * "idabonado_abonados"), inverseJoinColumns = @JoinColumn(name =
	 * "idservicio_servicios"))
	 * Set<ServiciosM> servSeleccionados = new HashSet<>();
	 */
	private Long usucrea;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea") */
	private LocalDate feccrea;
	private Long usumodi;
	/* @Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecmodi") */
	private LocalDate fecmodi;
	private Boolean adultomayor;
	private Boolean municipio;
	private Boolean swalcantarillado;
	private Long promedio;

	public Abonados() {
		super();
	}

	public Abonados(Long idabonado, String nromedidor, Long lecturainicial, Long estado, LocalDate fechainstalacion,
			String marca, Long secuencia, String direccionubicacion, String localizacion, String observacion,
			String departamento, String piso, Clientes idresponsable, Categorias idcategoria_categorias,
			Rutas idruta_rutas, Clientes idcliente_clientes, Ubicacionm idubicacion_ubicacion,
			Tipopago idtipopago_tipopago, Estadom idestadom_estadom, Long medidorprincipal, Long usucrea,
			LocalDate feccrea, Long usumodi, LocalDate fecmodi, Boolean adultomayor, Boolean municipio, Boolean swalcantarillado,
			Long promedio) {
		super();
		this.idabonado = idabonado;
		this.nromedidor = nromedidor;
		this.lecturainicial = lecturainicial;
		this.estado = estado;
		this.fechainstalacion = fechainstalacion;
		this.marca = marca;
		this.secuencia = secuencia;
		this.direccionubicacion = direccionubicacion;
		this.localizacion = localizacion;
		this.observacion = observacion;
		this.departamento = departamento;
		this.piso = piso;
		this.idresponsable = idresponsable;
		this.idcategoria_categorias = idcategoria_categorias;
		this.idruta_rutas = idruta_rutas;
		this.idcliente_clientes = idcliente_clientes;
		// this.idubicacionm_ubicacionm = idubicacionm_ubicacionm;
		this.idtipopago_tipopago = idtipopago_tipopago;
		this.idestadom_estadom = idestadom_estadom;
		this.medidorprincipal = medidorprincipal;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.adultomayor = adultomayor;
		this.municipio = municipio;
		this.swalcantarillado = swalcantarillado;
		this.promedio = promedio;
	}

	public Long getIdabonado() {
		return idabonado;
	}

	public Boolean getAdultomayor() {
		return adultomayor;
	}

	public void setAdultomayor(Boolean adultomayor) {
		this.adultomayor = adultomayor;
	}

	public Long getPromedio() {
		return promedio;
	}

	public void setPromedio(Long promedio) {
		this.promedio = promedio;
	}

	public Boolean getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Boolean municipio) {
		this.municipio = municipio;
	}

	public void setIdabonado(Long idabonado) {
		this.idabonado = idabonado;
	}

	public String getNromedidor() {
		return nromedidor;
	}

	public void setNromedidor(String nromedidor) {
		this.nromedidor = nromedidor;
	}

	public Long getEstado() {
		return estado;
	}

	public void setEstado(Long estado) {
		this.estado = estado;
	}

	public LocalDate getFechainstalacion() {
		return fechainstalacion;
	}

	public void setFechainstalacion(LocalDate fechainstalacion) {
		this.fechainstalacion = fechainstalacion;
	}

	public String getMarca() {
		return marca;
	}

	public void setMarca(String marca) {
		this.marca = marca;
	}

	public Long getSecuencia() {
		return secuencia;
	}

	public void setSecuencia(Long secuencia) {
		this.secuencia = secuencia;
	}

	public String getDireccionubicacion() {
		return direccionubicacion;
	}

	public void setDireccionubicacion(String direccionubicacion) {
		this.direccionubicacion = direccionubicacion;
	}

	public String getLocalizacion() {
		return localizacion;
	}

	public void setLocalizacion(String localizacion) {
		this.localizacion = localizacion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public String getDepartamento() {
		return departamento;
	}

	public void setDepartamento(String departamento) {
		this.departamento = departamento;
	}

	public String getPiso() {
		return piso;
	}

	public void setPiso(String piso) {
		this.piso = piso;
	}

	public Clientes getIdresponsable() {
		return idresponsable;
	}

	public void setIdresponsable(Clientes idresponsable) {
		this.idresponsable = idresponsable;
	}

	public Categorias getIdcategoria_categorias() {
		return idcategoria_categorias;
	}

	public void setIdcategoria_categorias(Categorias idcategoria_categorias) {
		this.idcategoria_categorias = idcategoria_categorias;
	}

	public Rutas getIdruta_rutas() {
		return idruta_rutas;
	}

	public void setIdruta_rutas(Rutas idruta_rutas) {
		this.idruta_rutas = idruta_rutas;
	}

	public Ubicacionm getIdubicacionm_ubicacionm() {
		return idubicacionm_ubicacionm;
	}

	public void setIdubicacionm_ubicacionm(Ubicacionm idubicacionm_ubicacionm) {
		this.idubicacionm_ubicacionm = idubicacionm_ubicacionm;
	}

	public Tipopago getIdtipopago_tipopago() {
		return idtipopago_tipopago;
	}

	public void setIdtipopago_tipopago(Tipopago idtipopago_tipopago) {
		this.idtipopago_tipopago = idtipopago_tipopago;
	}

	public Estadom getIdestadom_estadom() {
		return idestadom_estadom;
	}

	public void setIdestadom_estadom(Estadom idestadom_estadom) {
		this.idestadom_estadom = idestadom_estadom;
	}

	public Long getMedidorprincipal() {
		return medidorprincipal;
	}

	public void setMedidorprincipal(Long medidorprincipal) {
		this.medidorprincipal = medidorprincipal;
	}

	public Long getUsucrea() {
		return usucrea;
	}

	public void setUsucrea(Long usucrea) {
		this.usucrea = usucrea;
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

	public Clientes getIdcliente_clientes() {
		return idcliente_clientes;
	}

	public void setIdcliente_clientes(Clientes idcliente_clientes) {
		this.idcliente_clientes = idcliente_clientes;
	}

	public Long getLecturainicial() {
		return lecturainicial;
	}

	public void setLecturainicial(Long lecturainicial) {
		this.lecturainicial = lecturainicial;
	}

	public Boolean getSwalcantarillado() {
		return swalcantarillado;
	}

	public void setSwalcantarillado(Boolean swalcantarillado) {
		this.swalcantarillado = swalcantarillado;
	}

	/*
	 * public Set<ServiciosM> getServSeleccionados() {
	 * return servSeleccionados;
	 * }
	 * 
	 * public void setServSeleccionados(Set<ServiciosM> servSeleccionados) {
	 * this.servSeleccionados = servSeleccionados;
	 * }
	 * 
	 * public void addServicio(ServiciosM serviciosM) {
	 * servSeleccionados.add(serviciosM);
	 * }
	 */

}
