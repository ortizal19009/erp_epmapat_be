package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

	private Boolean ajenos;


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

}
