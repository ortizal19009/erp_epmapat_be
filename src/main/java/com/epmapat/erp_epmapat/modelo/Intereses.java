package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

import javax.persistence.*;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Interes") // <-- fuerza el nombre de entidad usado en JPQL
@Table(name = "intereses", uniqueConstraints = @UniqueConstraint(columnNames = { "anio",
		"mes" }), indexes = @Index(name = "idx_intereses_anio_mes", columnList = "anio,mes"))
public class Intereses {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idinteres;

	@Column(nullable = false)
	private Long anio;

	@Column(nullable = false)
	private Long mes; // 1..12

	@Column(nullable = false, precision = 18, scale = 6)
	private BigDecimal porcentaje; // % mensual

	private Long usucrea;

	@Column(name = "feccrea")
	private LocalDate feccrea;

	private Long usumodi;

	@Temporal(TemporalType.DATE)
	@Column(name = "fecmodi")
	private Date fecmodi;

}
