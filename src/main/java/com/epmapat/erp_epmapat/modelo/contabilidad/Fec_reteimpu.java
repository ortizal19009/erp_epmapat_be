package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "fec_retenciones_impuestos")

public class Fec_reteimpu implements Serializable {
   @Id
   private Long idretencionesimpuestos;
   private Long idretencion;
   private String codigo;
   private String codigoporcentaje;
   private BigDecimal baseimponible;
   private String codigodocumentosustento;
   private String numerodocumentosustento;
   private Date fechaemisiondocumentosustento;
   
   public Long getIdretencionesimpuestos() {
      return idretencionesimpuestos;
   }
   public void setIdretencionesimpuestos(Long idretencionesimpuestos) {
      this.idretencionesimpuestos = idretencionesimpuestos;
   }
   public Long getIdretencion() {
      return idretencion;
   }
   public void setIdretencion(Long idretencion) {
      this.idretencion = idretencion;
   }
   public String getCodigo() {
      return codigo;
   }
   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }
   public String getCodigoporcentaje() {
      return codigoporcentaje;
   }
   public void setCodigoporcentaje(String codigoporcentaje) {
      this.codigoporcentaje = codigoporcentaje;
   }
   public BigDecimal getBaseimponible() {
      return baseimponible;
   }
   public void setBaseimponible(BigDecimal baseimponible) {
      this.baseimponible = baseimponible;
   }
   public String getCodigodocumentosustento() {
      return codigodocumentosustento;
   }
   public void setCodigodocumentosustento(String codigodocumentosustento) {
      this.codigodocumentosustento = codigodocumentosustento;
   }
   public String getNumerodocumentosustento() {
      return numerodocumentosustento;
   }
   public void setNumerodocumentosustento(String numerodocumentosustento) {
      this.numerodocumentosustento = numerodocumentosustento;
   }
   public Date getFechaemisiondocumentosustento() {
      return fechaemisiondocumentosustento;
   }
   public void setFechaemisiondocumentosustento(Date fechaemisiondocumentosustento) {
      this.fechaemisiondocumentosustento = fechaemisiondocumentosustento;
   }

   
}
