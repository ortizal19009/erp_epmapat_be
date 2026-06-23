package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "partixcerti")

public class Partixcerti {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idparxcer;
	private String descripcion;
	private BigDecimal valor;
	private BigDecimal saldo;
	private BigDecimal totprmisos;
	private Short swreinte;
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
	private Long inteje;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intpre")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Presupue intpre;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcerti")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Certipresu idcerti;
	private Long idparxcer_;

}
