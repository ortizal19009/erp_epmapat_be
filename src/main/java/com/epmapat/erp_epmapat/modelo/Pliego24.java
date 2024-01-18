package com.epmapat.erp_epmapat.modelo;

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
@Table(name = "pliego24")

public class Pliego24 {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idpliego;
   private Integer desde;
   private Integer hasta;
   private BigDecimal agua;
   private BigDecimal saneamiento;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idcategoria")
   private Categorias idcategoria;
   // private Integer gradualidad;
   private BigDecimal porc;

   public Long getIdpliego() {
      return idpliego;
   }

   public void setIdpliego(Long idpliego) {
      this.idpliego = idpliego;
   }

   public Integer getDesde() {
      return desde;
   }

   public void setDesde(Integer desde) {
      this.desde = desde;
   }

   public Integer getHasta() {
      return hasta;
   }

   public void setHasta(Integer hasta) {
      this.hasta = hasta;
   }

   public BigDecimal getAgua() {
      return agua;
   }

   public void setAgua(BigDecimal agua) {
      this.agua = agua;
   }

   public BigDecimal getSaneamiento() {
      return saneamiento;
   }

   public void setSaneamiento(BigDecimal saneamiento) {
      this.saneamiento = saneamiento;
   }

   public Categorias getIdcategoria() {
      return idcategoria;
   }

   public void setIdcategoria(Categorias idcategoria) {
      this.idcategoria = idcategoria;
   }

   // public Integer getGradualidad() {
   //    return gradualidad;
   // }
   // public void setGradualidad(Integer gradualidad) {
   //    this.gradualidad = gradualidad;
   // }

   public BigDecimal getPorc() {
      return porc;
   }

   public void setPorc(BigDecimal porc) {
      this.porc = porc;
   }

}
