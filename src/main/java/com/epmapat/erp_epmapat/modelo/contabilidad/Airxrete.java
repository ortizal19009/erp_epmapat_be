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

@Entity
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

   public Long getIdairxrete() {
      return idairxrete;
   }

   public void setIdairxrete(Long idairxrete) {
      this.idairxrete = idairxrete;
   }

   public BigDecimal getBaseimpair0() {
      return baseimpair0;
   }

   public void setBaseimpair0(BigDecimal baseimpair0) {
      this.baseimpair0 = baseimpair0;
   }

   public BigDecimal getBaseimpair12() {
      return baseimpair12;
   }

   public void setBaseimpair12(BigDecimal baseimpair12) {
      this.baseimpair12 = baseimpair12;
   }

   public BigDecimal getBaseimpairno() {
      return baseimpairno;
   }

   public void setBaseimpairno(BigDecimal baseimpairno) {
      this.baseimpairno = baseimpairno;
   }

   public BigDecimal getBaseimpair() {
      return baseimpair;
   }

   public void setBaseimpair(BigDecimal baseimpair) {
      this.baseimpair = baseimpair;
   }

   public BigDecimal getValretair() {
      return valretair;
   }

   public void setValretair(BigDecimal valretair) {
      this.valretair = valretair;
   }

   public Retenciones getIdrete() {
      return idrete;
   }

   public void setIdrete(Retenciones idrete) {
      this.idrete = idrete;
   }

   public Tabla10 getIdtabla10() {
      return idtabla10;
   }

   public void setIdtabla10(Tabla10 idtabla10) {
      this.idtabla10 = idtabla10;
   } 

}
