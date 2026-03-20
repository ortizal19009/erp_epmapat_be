package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.*;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="conciliaban")
public class ConciliaBan {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idconcilia; 
	private Integer mes; 
	private BigDecimal libinicial; 
	private BigDecimal libdebitos; 
	private BigDecimal libcreditos; 
	private BigDecimal libdepositos; 
	private BigDecimal libcheques; 
	private BigDecimal liberrores; 
	private BigDecimal baninicial; 
	private BigDecimal bancreditos; 
	private BigDecimal bandebitos; 
	private BigDecimal bancheaa; 
	private BigDecimal bannc; 
	private BigDecimal bannd; 
	private BigDecimal banerrores; 
	private String observa; 
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcuenta")
	private Cuentas idcuenta;

}
