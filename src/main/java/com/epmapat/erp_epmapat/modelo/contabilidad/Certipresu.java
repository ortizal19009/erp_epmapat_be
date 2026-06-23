package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;
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
@Table(name = "certificaciones")
public class Certipresu {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcerti;
	private Integer tipo;
	private Long numero;
	
	private LocalDate fecha;
	private BigDecimal valor;
	private String descripcion;
	private String numdoc;
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
	@JoinColumn(name = "idbene")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Beneficiarios idbene;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbeneres")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Beneficiarios idbeneres;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Documentos intdoc;
	
}
