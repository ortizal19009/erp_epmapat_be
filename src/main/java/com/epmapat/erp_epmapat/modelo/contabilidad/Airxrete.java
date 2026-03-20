package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "airxrete")

public class Airxrete {
   
  	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idairxrete; 

   private BigDecimal baseimpair0; 
   private BigDecimal baseimpair12; 
   private BigDecimal baseimpairno; 
   private BigDecimal baseimpair; 
   private BigDecimal valretair;

   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idrete")
	private Retenciones idrete; 

   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtabla10")
	private Tabla10 idtabla10;

}
