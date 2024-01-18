package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "lecturas")

public class Lecturas implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idlectura;
   Integer estado;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idrutaxemision_rutasxemision")
   private Rutasxemision idrutaxemision_rutasxemision;
   Date fechaemision;
   Float lecturaanterior;
   Float lecturaactual;
   Float lecturadigitada;
   Integer mesesmulta;
   String observaciones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idnovedad_novedades")
   private Novedad idnovedad_novedades;
   Long idemision;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idabonado_abonados")
   private Abonados idabonado_abonados;
   Long idresponsable;
   Long idcategoria;
   Long idfactura;
   private BigDecimal total1;
   private BigDecimal total31;
   private BigDecimal total32;

   public Date getFechaemision() {
      return fechaemision;
   }

   public Long getIdemision() {
      return idemision;
   }

   public void setIdemision(Long idemision) {
      this.idemision = idemision;
   }

   public Abonados getIdabonado_abonados() {
      return idabonado_abonados;
   }

   public void setIdabonado_abonados(Abonados idabonado_abonados) {
      this.idabonado_abonados = idabonado_abonados;
   }

   public Long getIdresponsable() {
      return idresponsable;
   }

   public void setIdresponsable(Long idresponsable) {
      this.idresponsable = idresponsable;
   }

   public Long getIdcategoria() {
      return idcategoria;
   }

   public void setIdcategoria(Long idcategoria) {
      this.idcategoria = idcategoria;
   }

   public void setFechaemision(Date fechaemision) {
      this.fechaemision = fechaemision;
   }

   public Float getLecturaanterior() {
      return lecturaanterior;
   }

   public void setLecturaanterior(Float lecturaanterior) {
      this.lecturaanterior = lecturaanterior;
   }

   public Float getLecturaactual() {
      return lecturaactual;
   }

   public void setLecturaactual(Float lecturaactual) {
      this.lecturaactual = lecturaactual;
   }

   public Float getLecturadigitada() {
      return lecturadigitada;
   }

   public void setLecturadigitada(Float lecturadigitada) {
      this.lecturadigitada = lecturadigitada;
   }

   public Integer getMesesmulta() {
      return mesesmulta;
   }

   public void setMesesmulta(Integer mesesmulta) {
      this.mesesmulta = mesesmulta;
   }

   public String getObservaciones() {
      return observaciones;
   }

   public void setObservaciones(String observaciones) {
      this.observaciones = observaciones;
   }

   public Long getIdlectura() {
      return idlectura;
   }

   public void setIdlectura(Long idlectura) {
      this.idlectura = idlectura;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public Rutasxemision getIdrutaxemision_rutasxemision() {
      return idrutaxemision_rutasxemision;
   }

   public void setIdrutaxemision_rutasxemision(Rutasxemision idrutaxemision_rutasxemision) {
      this.idrutaxemision_rutasxemision = idrutaxemision_rutasxemision;
   }

   public Novedad getIdnovedad_novedades() {
      return idnovedad_novedades;
   }

   public void setIdnovedad_novedades(Novedad idnovedad_novedades) {
      this.idnovedad_novedades = idnovedad_novedades;
   }

   public Long getIdfactura() {
      return idfactura;
   }

   public void setIdfactura(Long idfactura) {
      this.idfactura = idfactura;
   }

   public BigDecimal getTotal1() {
      return total1;
   }

   public void setTotal1(BigDecimal total1) {
      this.total1 = total1;
   }

   public BigDecimal getTotal31() {
      return total31;
   }

   public void setTotal31(BigDecimal total31) {
      this.total31 = total31;
   }

   public BigDecimal getTotal32() {
      return total32;
   }

   public void setTotal32(BigDecimal total32) {
      this.total32 = total32;
   }

}