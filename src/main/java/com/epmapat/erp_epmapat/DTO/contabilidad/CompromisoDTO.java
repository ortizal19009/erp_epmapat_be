package com.epmapat.erp_epmapat.DTO.contabilidad;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class CompromisoDTO {

   // Atributos de Ejecucio
   private Long inteje;
   private String codpar;
   private int prmiso;
   private int totdeven;
   @Temporal(TemporalType.DATE)
    @DateTimeFormat(iso = ISO.DATE)
    @Column(name = "fecha_eje")
    private Date fecha_eje;
   // private Date fecha_eje ;
   // ... otros atributos de Ejecucio

   public Date getFecha_eje() {
      return fecha_eje;
   }
   public void setFecha_eje(Date fecha_eje) {
      this.fecha_eje = fecha_eje;
   }
   // Atributos de Tramites
   private Long idtrami;
   private Date fecha;
   // ... otros atributos de Tramites

   // Atributos de Beneficiarios
   private Long idbene;
   private String nomben;
   // ... otros atributos de Beneficiarios
   public Long getInteje() {
      return inteje;
   }
   public void setInteje(Long inteje) {
      this.inteje = inteje;
   }
   public String getCodpar() {
      return codpar;
   }
   public void setCodpar(String codpar) {
      this.codpar = codpar;
   }
   public int getPrmiso() {
      return prmiso;
   }
   public void setPrmiso(int prmiso) {
      this.prmiso = prmiso;
   }
   public int getTotdeven() {
      return totdeven;
   }
   public void setTotdeven(int totdeven) {
      this.totdeven = totdeven;
   }
   public Long getIdtrami() {
      return idtrami;
   }
   public void setIdtrami(Long idtrami) {
      this.idtrami = idtrami;
   }
   public Date getFecha() {
      return fecha;
   }
   public void setFecha(Date fecha) {
      this.fecha = fecha;
   }
   public Long getIdbene() {
      return idbene;
   }
   public void setIdbene(Long idbene) {
      this.idbene = idbene;
   }
   public String getNomben() {
      return nomben;
   }
   public void setNomben(String nomben) {
      this.nomben = nomben;
   }

}
