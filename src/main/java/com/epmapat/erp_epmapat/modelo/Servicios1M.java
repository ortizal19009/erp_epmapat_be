package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "servicios1")

public class Servicios1M {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idservicio; 
	private String descripcion; 
	private Long estado; 
	private Long swconsumo;
	private Float valor; 
	private Long swiva;
	private Long swinvent;
	private Long facturable; 
	private String nombre; 
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
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idmodulo_modulos")
	private Modulos idmodulo_modulos;
	private Long tipocalculo; 
	private Float serviadmi;
	private Float descuento; 
	private Float otro;

	public Servicios1M() {
		super();
	}

	public Servicios1M(Long idservicio, String descripcion, Long estado, Long swconsumo, Float valor, Long swiva,
			Long swinvent, Long facturable, String nombre, Long usucrea, Date feccrea, Long usumodi, Date fecmodi,
			Modulos idmodulo_modulos, Long tipocalculo, Float serviadmi, Float descuento, Float otro) {
		super();
		this.idservicio = idservicio;
		this.descripcion = descripcion;
		this.estado = estado;
		this.swconsumo = swconsumo;
		this.valor = valor;
		this.swiva = swiva;
		this.swinvent = swinvent;
		this.facturable = facturable;
		this.nombre = nombre;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
		this.idmodulo_modulos = idmodulo_modulos;
		this.tipocalculo = tipocalculo;
		this.serviadmi = serviadmi;
		this.descuento = descuento;
		this.otro = otro;
	}
	
	public Long getIdservicio() {
		return idservicio;
	}
	public void setIdservicio(Long idservicio) {
		this.idservicio = idservicio;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
	}
	public Long getSwconsumo() {
		return swconsumo;
	}
	public void setSwconsumo(Long swconsumo) {
		this.swconsumo = swconsumo;
	}
	public Float getValor() {
		return valor;
	}
	public void setValor(Float valor) {
		this.valor = valor;
	}
	public Long getSwiva() {
		return swiva;
	}
	public void setSwiva(Long swiva) {
		this.swiva = swiva;
	}
	public Long getSwinvent() {
		return swinvent;
	}
	public void setSwinvent(Long swinvent) {
		this.swinvent = swinvent;
	}
	public Long getFacturable() {
		return facturable;
	}
	public void setFacturable(Long facturable) {
		this.facturable = facturable;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
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
	public Modulos getIdmodulo_modulos() {
		return idmodulo_modulos;
	}
	public void setIdmodulo_modulos(Modulos idmodulo_modulos) {
		this.idmodulo_modulos = idmodulo_modulos;
	}
	public Long getTipocalculo() {
		return tipocalculo;
	}
	public void setTipocalculo(Long tipocalculo) {
		this.tipocalculo = tipocalculo;
	}
	public Float getServiadmi() {
		return serviadmi;
	}
	public void setServiadmi(Float serviadmi) {
		this.serviadmi = serviadmi;
	}
	public Float getDescuento() {
		return descuento;
	}
	public void setDescuento(Float descuento) {
		this.descuento = descuento;
	}
	public Float getOtro() {
		return otro;
	}
	public void setOtro(Float otro) {
		this.otro = otro;
	}
	
}
