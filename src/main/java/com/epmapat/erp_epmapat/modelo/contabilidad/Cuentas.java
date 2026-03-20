package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.LocalDate;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "cuentas")
public class Cuentas {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idcuenta;
	private String codcue;
	private String nomcue;
	private String grucue;
	private Integer nivcue;
	private Integer movcue;
	private String asodebe;
	private String asohaber;
	private BigDecimal debito;
	private BigDecimal credito;
	private BigDecimal saldo;
	private BigDecimal balance;
	private Long intgrupo;
	private Integer sigef;
	private Integer tiptran;
	private Long usucrea;
	private LocalDate feccrea;

	private Long usumodi;
	private LocalDate fecmodi;
	private Long grufluefec;
	private Long resulcostos;
	private Long balancostos;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idnivel")
	private Niveles idnivel;

}
