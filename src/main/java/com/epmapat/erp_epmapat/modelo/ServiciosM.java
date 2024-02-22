package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "servicios")

public class ServiciosM {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idservicio;
	private String nombre;
	private Long swconsumo;
	private Long estado;
	private Float valor;
	private Long swiva; 
	private Long swinvent; 
	private Long facturable;
	private String descripcion; 
	/*@JsonIgnore
	@ManyToMany(mappedBy = "servSeleccionados")
	Set<Abonados> aboSeleccionados = new HashSet<>();*/
	private Long usucrea; 
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name="feccrea")
	private Date feccrea;
	private Long usumodi;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso=ISO.DATE)
	@Column(name="fecmodi")
	private Date fecmodi;
	public ServiciosM() {
		super();
		
	}
	public ServiciosM(Long idservicio, String nombre, Long swconsumo, Long estado, Float valor, Long swiva, Long swinvent,
			Long facturable, String descripcion, Long usucrea, Date feccrea, Long usumodi, Date fecmodi) {
		super();
		this.idservicio = idservicio;
		this.nombre = nombre;
		this.swconsumo = swconsumo;
		this.estado = estado;
		this.valor = valor;
		this.swiva = swiva;
		this.swinvent = swinvent;
		this.facturable = facturable;
		this.descripcion = descripcion;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}
	public Long getIdservicio() {
		return idservicio;
	}
	public void setIdservicio(Long idservicio) {
		this.idservicio = idservicio;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Long getSwconsumo() {
		return swconsumo;
	}
	public void setSwconsumo(Long swconsumo) {
		this.swconsumo = swconsumo;
	}
	public Long getEstado() {
		return estado;
	}
	public void setEstado(Long estado) {
		this.estado = estado;
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
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
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
	/*public Set<Abonados> getAboSeleccionados() {
		return aboSeleccionados;
	}
	public void setAboSeleccionados(Set<Abonados> aboSeleccionados) {
		this.aboSeleccionados = aboSeleccionados;
	}*/

}
