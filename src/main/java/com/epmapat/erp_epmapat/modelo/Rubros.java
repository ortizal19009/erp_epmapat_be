package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;


@Entity
@Table(name = "rubros")

public class Rubros {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrubro;
	private String descripcion;
	private Boolean estado;
	private Boolean calculable;
	private BigDecimal valor;
	private Boolean swiva;
	private Integer tipo;
	private Long esiva;
	private Long esdebito;
	private Long facturable;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idmodulo_modulos")
	private Modulos idmodulo_modulos;
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

	/*
	 * @JsonIgnore
	 * 
	 * @ManyToMany(mappedBy = "rubros")
	 * public Set<Facturas> facturas = new HashSet<>();
	 * 
	 * public Set<Facturas> getFacturas() {
	 * return facturas;
	 * }
	 * 
	 * public void setFacturas(Set<Facturas> facturas) {
	 * this.facturas = facturas;
	 * }
	 */
	public Rubros() {
		super();
	}

	public Rubros(Long idrubro, String descripcion, Boolean estado, Boolean calculable, BigDecimal valor, Boolean swiva,
			Integer tipo, Long esiva, Long esdebito, Long facturable, Modulos idmodulo_modulos, Long usucrea,
			Date feccrea, Long usumodi, Date fecmodi) {
		this.idrubro = idrubro;
		this.descripcion = descripcion;
		this.estado = estado;
		this.calculable = calculable;
		this.valor = valor;
		this.swiva = swiva;
		this.tipo = tipo;
		this.esiva = esiva;
		this.esdebito = esdebito;
		this.facturable = facturable;
		this.idmodulo_modulos = idmodulo_modulos;
		this.usucrea = usucrea;
		this.feccrea = feccrea;
		this.usumodi = usumodi;
		this.fecmodi = fecmodi;
	}

	public Long getIdrubro() {
		return idrubro;
	}

	public void setIdrubro(Long idrubro) {
		this.idrubro = idrubro;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public Boolean getCalculable() {
		return calculable;
	}

	public void setCalculable(Boolean calculable) {
		this.calculable = calculable;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Boolean getSwiva() {
		return swiva;
	}

	public void setSwiva(Boolean swiva) {
		this.swiva = swiva;
	}

	public Integer getTipo() {
		return tipo;
	}

	public void setTipo(Integer tipo) {
		this.tipo = tipo;
	}

	public Long getEsiva() {
		return esiva;
	}

	public void setEsiva(Long esiva) {
		this.esiva = esiva;
	}

	public Long getEsdebito() {
		return esdebito;
	}

	public void setEsdebito(Long esdebito) {
		this.esdebito = esdebito;
	}

	public Long getFacturable() {
		return facturable;
	}

	public void setFacturable(Long facturable) {
		this.facturable = facturable;
	}

	public Modulos getIdmodulo_modulos() {
		return idmodulo_modulos;
	}

	public void setIdmodulo_modulos(Modulos idmodulo_modulos) {
		this.idmodulo_modulos = idmodulo_modulos;
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
