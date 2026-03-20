package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
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
	private Beneficiarios idbene;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idbeneres")
	private Beneficiarios idbeneres;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "intdoc")
	private Documentos intdoc;
	
}
