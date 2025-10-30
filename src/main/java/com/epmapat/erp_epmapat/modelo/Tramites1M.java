package com.epmapat.erp_epmapat.modelo;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name="tramites1")

public class Tramites1M {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtramite;

	private Float valor;
	private String descripcion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idabonado_abonados")
	private Abonados idabonado_abonados;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fecha")
	private Date fecha;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "validohasta")
	private Date validohasta;
	@ManyToMany
	@JoinTable(name = "rubrosxtramite", joinColumns = @JoinColumn(name = "idtramite_tramites"), inverseJoinColumns = @JoinColumn(name = "idrubro_rubros"))
	Set<Rubros> rubrosSeleccionados = new HashSet<>();
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

	public Tramites1M() {
		super();
	}

	public Tramites1M(Long idtramite, Float valor, String descripcion, Clientes idcliente_clientes,
			Abonados idabonado_abonados, Date fecha, Date validohasta, Long usucrea, Date feccrea, Long usumodi,
			Date fecmodi) {
		super();
		this.idtramite = idtramite;
		this.valor = valor;
		this.descripcion = descripcion;
		this.idcliente_clientes = idcliente_clientes;
		this.idabonado_abonados = idabonado_abonados;
		this.fecha = fecha;
		this.validohasta = validohasta;
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

	public Float getValor() {
		return valor;
	}

	public void setValor(Float valor) {
		this.valor = valor;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Clientes getIdcliente_clientes() {
		return idcliente_clientes;
	}

	public void setIdcliente_clientes(Clientes idcliente_clientes) {
		this.idcliente_clientes = idcliente_clientes;
	}

	public Abonados getIdabonado_abonados() {
		return idabonado_abonados;
	}

	public void setIdabonado_abonados(Abonados idabonado_abonados) {
		this.idabonado_abonados = idabonado_abonados;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public Date getValidohasta() {
		return validohasta;
	}

	public void setValidohasta(Date validohasta) {
		this.validohasta = validohasta;
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

	public void addRubros(Rubros rubrosM) {
		rubrosSeleccionados.add(rubrosM);
	}

}
