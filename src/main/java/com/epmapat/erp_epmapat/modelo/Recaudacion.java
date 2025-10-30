package com.epmapat.erp_epmapat.modelo;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "recaudacion")

public class Recaudacion {
   
   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idrecaudacion;
	
	@Column(name = "fechacobro")
	private ZonedDateTime fechacobro;
   
   private Long recaudador;
   private BigDecimal totalpagar;
   private BigDecimal recibo;
   private BigDecimal cambio;
   private Long formapago;
   private BigDecimal valor;
   private Integer estado;
   private Date fechaeliminacion;
   private Long usuarioeliminacion;
   private String observaciones;
   private Long ncnumero;
   private BigDecimal ncvalor;
   private Long usucrea;
   
   @Column(name = "feccrea")
	private ZonedDateTime feccrea;

   public Long getIdrecaudacion() {
      return idrecaudacion;
   }

   public void setIdrecaudacion(Long idrecaudacion) {
      this.idrecaudacion = idrecaudacion;
   }

   public ZonedDateTime getFechacobro() {
      return fechacobro;
   }

   public void setFechacobro(ZonedDateTime fechacobro) {
      this.fechacobro = fechacobro;
   }

   public Long getRecaudador() {
      return recaudador;
   }

   public void setRecaudador(Long recaudador) {
      this.recaudador = recaudador;
   }

   public BigDecimal getTotalpagar() {
      return totalpagar;
   }

   public void setTotalpagar(BigDecimal totalpagar) {
      this.totalpagar = totalpagar;
   }

   public BigDecimal getRecibo() {
      return recibo;
   }

   public void setRecibo(BigDecimal recibo) {
      this.recibo = recibo;
   }

   public BigDecimal getCambio() {
      return cambio;
   }

   public void setCambio(BigDecimal cambio) {
      this.cambio = cambio;
   }

   public Long getFormapago() {
      return formapago;
   }

   public void setFormapago(Long formapago) {
      this.formapago = formapago;
   }

   public BigDecimal getValor() {
      return valor;
   }

   public void setValor(BigDecimal valor) {
      this.valor = valor;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public Date getFechaeliminacion() {
      return fechaeliminacion;
   }

   public void setFechaeliminacion(Date fechaeliminacion) {
      this.fechaeliminacion = fechaeliminacion;
   }

   public Long getUsuarioeliminacion() {
      return usuarioeliminacion;
   }

   public void setUsuarioeliminacion(Long usuarioeliminacion) {
      this.usuarioeliminacion = usuarioeliminacion;
   }

   public String getObservaciones() {
      return observaciones;
   }

   public void setObservaciones(String observaciones) {
      this.observaciones = observaciones;
   }

   public Long getNcnumero() {
      return ncnumero;
   }

   public void setNcnumero(Long ncnumero) {
      this.ncnumero = ncnumero;
   }

   public BigDecimal getNcvalor() {
      return ncvalor;
   }

   public void setNcvalor(BigDecimal ncvalor) {
      this.ncvalor = ncvalor;
   }

   public Long getUsucrea() {
      return usucrea;
   }

   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }

   public ZonedDateTime getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(ZonedDateTime feccrea) {
      this.feccrea = feccrea;
   }

   
}
