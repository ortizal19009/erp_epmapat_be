package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "categorias")

public class Categorias {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcategoria;
	private String codigo;
	private String descripcion;
	private Boolean habilitado;
	private BigDecimal fijoagua;
	private BigDecimal fijosanea;
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
	
	

	
	public String getCodigo() {
		return codigo;
	}

	public BigDecimal getFijoagua() {
		return fijoagua;
	}

	public void setFijoagua(BigDecimal fijoagua) {
		this.fijoagua = fijoagua;
	}

	public BigDecimal getFijosanea() {
		return fijosanea;
	}

	public void setFijosanea(BigDecimal fijosanea) {
		this.fijosanea = fijosanea;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Boolean getHabilitado() {
		return habilitado;
	}

	public void setHabilitado(Boolean habilitado) {
		this.habilitado = habilitado;
	}

	public Categorias() {
		super();
	}

	public Long getIdcategoria() {
		return idcategoria;
	}

	public void setIdcategoria(Long idcategoria) {
		this.idcategoria = idcategoria;
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

}
