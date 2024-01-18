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
@Table(name = "rutasxemision")

public class Rutasxemision implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idrutaxemision;
   Integer estado;
   Long usuariocierre;
   Date fechacierre;
   Long usucrea;
   Date feccrea;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idemision_emisiones")
   private Emisiones idemision_emisiones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idruta_rutas")
   private Rutas idruta_rutas;
   private Long m3;
   private BigDecimal total;

   public Rutasxemision(Long idrutaxemision, Integer estado, Long usuariocierre, Date fechacierre, Long usucrea,
         Date feccrea, Emisiones idemision_emisiones, Rutas idruta_rutas, Long m3, BigDecimal total) {
      this.idrutaxemision = idrutaxemision;
      this.estado = estado;
      this.usuariocierre = usuariocierre;
      this.fechacierre = fechacierre;
      this.usucrea = usucrea;
      this.feccrea = feccrea;
      this.idemision_emisiones = idemision_emisiones;
      this.idruta_rutas = idruta_rutas;
      this.m3 = m3;
   }
   
   public Rutasxemision() {
   }

   public Long getIdrutaxemision() {
      return idrutaxemision;
   }

   public void setIdrutaxemision(Long idrutaxemision) {
      this.idrutaxemision = idrutaxemision;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public Long getUsuariocierre() {
      return usuariocierre;
   }

   public void setUsuariocierre(Long usuariocierre) {
      this.usuariocierre = usuariocierre;
   }

   public Date getFechacierre() {
      return fechacierre;
   }

   public void setFechacierre(Date fechacierre) {
      this.fechacierre = fechacierre;
   }

   public Long getUsucrea() {
      return usucrea;
   }

   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }

   public Date getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(Date feccrea) {
      this.feccrea = feccrea;
   }

   public Emisiones getIdemision_emisiones() {
      return idemision_emisiones;
   }

   public void setIdemision_emisiones(Emisiones idemision_emisiones) {
      this.idemision_emisiones = idemision_emisiones;
   }

   public Rutas getIdruta_rutas() {
      return idruta_rutas;
   }

   public void setIdruta_rutas(Rutas idruta_rutas) {
      this.idruta_rutas = idruta_rutas;
   }

   public Long getM3() {
      return m3;
   }

   public void setM3(Long m3) {
      this.m3 = m3;
   }

   public BigDecimal getTotal() {
      return total;
   }

   public void setTotal(BigDecimal total) {
      this.total = total;
   }

}
