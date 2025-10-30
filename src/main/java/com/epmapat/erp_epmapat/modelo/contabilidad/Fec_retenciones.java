package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fec_retenciones")

public class Fec_retenciones implements Serializable {
   @Id
   private Long idretencion;
   private String claveacceso;
   private String secuencial;
   private String xmlautorizado;
   private String errores;
   private String estado;
   private String establecimiento;
   private String puntoemision;
   private String direccionestablecimiento;
   private LocalDate fechaemision;

   private String tipoidentificacionsujetoretenid;

   private String razonsocialsujetoretenido;
   private String identificacionsujetoretenido;
   private String periodofiscal;
   private String telefonosujetoretenido;
   private String emailsujetoretenido;

   public Long getIdretencion() {
      return idretencion;
   }

   public void setIdretencion(Long idretencion) {
      this.idretencion = idretencion;
   }

   public String getClaveacceso() {
      return claveacceso;
   }

   public void setClaveacceso(String claveacceso) {
      this.claveacceso = claveacceso;
   }

   public String getSecuencial() {
      return secuencial;
   }

   public void setSecuencial(String secuencial) {
      this.secuencial = secuencial;
   }

   public String getXmlautorizado() {
      return xmlautorizado;
   }

   public void setXmlautorizado(String xmlautorizado) {
      this.xmlautorizado = xmlautorizado;
   }

   public String getErrores() {
      return errores;
   }

   public void setErrores(String errores) {
      this.errores = errores;
   }

   public String getEstado() {
      return estado;
   }

   public void setEstado(String estado) {
      this.estado = estado;
   }

   public String getEstablecimiento() {
      return establecimiento;
   }

   public void setEstablecimiento(String establecimiento) {
      this.establecimiento = establecimiento;
   }

   public String getPuntoemision() {
      return puntoemision;
   }

   public void setPuntoemision(String puntoemision) {
      this.puntoemision = puntoemision;
   }

   public String getDireccionestablecimiento() {
      return direccionestablecimiento;
   }

   public void setDireccionestablecimiento(String direccionestablecimiento) {
      this.direccionestablecimiento = direccionestablecimiento;
   }

   public LocalDate getFechaemision() {
      return fechaemision;
   }

   public void setFechaemision(LocalDate fechaemision) {
      this.fechaemision = fechaemision;
   }

   public String getTipoidentificacionsujetoretenid() {
      return tipoidentificacionsujetoretenid;
   }

   public void setTipoidentificacionsujetoretenid(String tipoidentificacionsujetoretenid) {
      this.tipoidentificacionsujetoretenid = tipoidentificacionsujetoretenid;
   }

   public String getRazonsocialsujetoretenido() {
      return razonsocialsujetoretenido;
   }

   public void setRazonsocialsujetoretenido(String razonsocialsujetoretenido) {
      this.razonsocialsujetoretenido = razonsocialsujetoretenido;
   }

   public String getIdentificacionsujetoretenido() {
      return identificacionsujetoretenido;
   }

   public void setIdentificacionsujetoretenido(String identificacionsujetoretenido) {
      this.identificacionsujetoretenido = identificacionsujetoretenido;
   }

   public String getPeriodofiscal() {
      return periodofiscal;
   }

   public void setPeriodofiscal(String periodofiscal) {
      this.periodofiscal = periodofiscal;
   }

   public String getTelefonosujetoretenido() {
      return telefonosujetoretenido;
   }

   public void setTelefonosujetoretenido(String telefonosujetoretenido) {
      this.telefonosujetoretenido = telefonosujetoretenido;
   }

   public String getEmailsujetoretenido() {
      return emailsujetoretenido;
   }

   public void setEmailsujetoretenido(String emailsujetoretenido) {
      this.emailsujetoretenido = emailsujetoretenido;
   }

}
