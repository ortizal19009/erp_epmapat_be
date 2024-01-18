package com.epmapat.erp_epmapat.modelo;

import java.time.LocalTime;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Usuarios;

@Entity
@Table(name = "recaudaxcaja")

public class Recaudaxcaja {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idrecaudaxcaja;
   private Integer estado;
   private Long facinicio;
   private Long facfin;
   private Date fechainiciolabor;
   private Date fechafinlabor;
   private LocalTime horainicio;
   private LocalTime horafin;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idcaja_cajas")
   private Cajas idcaja_cajas;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idusuario_usuarios")
   private Usuarios idusuario_usuarios;

   public Long getIdrecaudaxcaja() {
      return idrecaudaxcaja;
   }

   public void setIdrecaudaxcaja(Long idrecaudaxcaja) {
      this.idrecaudaxcaja = idrecaudaxcaja;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public Long getFacinicio() {
      return facinicio;
   }

   public void setFacinicio(Long facinicio) {
      this.facinicio = facinicio;
   }

   public Long getFacfin() {
      return facfin;
   }

   public void setFacfin(Long facfin) {
      this.facfin = facfin;
   }

   public Date getFechainiciolabor() {
      return fechainiciolabor;
   }

   public void setFechainiciolabor(Date fechainiciolabor) {
      this.fechainiciolabor = fechainiciolabor;
   }

   public Date getFechafinlabor() {
      return fechafinlabor;
   }

   public void setFechafinlabor(Date fechafinlabor) {
      this.fechafinlabor = fechafinlabor;
   }

   public LocalTime getHorainicio() {
      return horainicio;
   }

   public void setHorainicio(LocalTime horainicio) {
      this.horainicio = horainicio;
   }

   public LocalTime getHorafin() {
      return horafin;
   }

   public void setHorafin(LocalTime horafin) {
      this.horafin = horafin;
   }

   public Cajas getIdcaja_cajas() {
      return idcaja_cajas;
   }

   public void setIdcaja_cajas(Cajas idcaja_cajas) {
      this.idcaja_cajas = idcaja_cajas;
   }

   public Usuarios getIdusuario_usuarios() {
      return idusuario_usuarios;
   }

   public void setIdusuario_usuarios(Usuarios idusuario_usuarios) {
      this.idusuario_usuarios = idusuario_usuarios;
   }

   
}
