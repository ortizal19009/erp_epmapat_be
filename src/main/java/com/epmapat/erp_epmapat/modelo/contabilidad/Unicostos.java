package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "unicostos")

public class Unicostos {
   
   @Id
   private Integer mes;
	private BigDecimal agua;
   private BigDecimal aguaprod;
   private BigDecimal alcanta;
   
   public Integer getMes() {
      return mes;
   }
   public void setMes(Integer mes) {
      this.mes = mes;
   }
   public BigDecimal getAgua() {
      return agua;
   }
   public void setAgua(BigDecimal agua) {
      this.agua = agua;
   }
   public BigDecimal getAguaprod() {
      return aguaprod;
   }
   public void setAguaprod(BigDecimal aguaprod) {
      this.aguaprod = aguaprod;
   }
   public BigDecimal getAlcanta() {
      return alcanta;
   }
   public void setAlcanta(BigDecimal alcanta) {
      this.alcanta = alcanta;
   }

}
