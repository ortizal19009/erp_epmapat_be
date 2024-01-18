package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.time.ZonedDateTime;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.Clasificador;

@Entity
@Table(name = "presupue")

public class Presupue {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idpresupue;
   
   private Integer tippar;
   private String codpar;
   private String codigo;
   private String nompar;
   private Double inicia;
   private Double totmod;
   private Double totcerti;
   private Double totmisos;
   private Double totdeven;
   private String funcion;

   private Long idestrfunc;
   String codacti;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intcla")
   private Clasificador intcla;

   private String codpart;
   private Boolean swpluri;

   private Integer usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;

   private Integer usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;

   public Long getIdpresupue() {
      return idpresupue;
   }

   public void setIdpresupue(Long idpresupue) {
      this.idpresupue = idpresupue;
   }

   public Integer getTippar() {
      return tippar;
   }

   public void setTippar(Integer tippar) {
      this.tippar = tippar;
   }

   public String getCodpar() {
      return codpar;
   }

   public void setCodpar(String codpar) {
      this.codpar = codpar;
   }

   public String getCodigo() {
      return codigo;
   }

   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }

   public String getNompar() {
      return nompar;
   }

   public void setNompar(String nompar) {
      this.nompar = nompar;
   }

   public Double getInicia() {
      return inicia;
   }

   public void setInicia(Double inicia) {
      this.inicia = inicia;
   }

   public Double getTotmod() {
      return totmod;
   }

   public void setTotmod(Double totmod) {
      this.totmod = totmod;
   }

   public Double getTotcerti() {
      return totcerti;
   }

   public void setTotcerti(Double totcerti) {
      this.totcerti = totcerti;
   }

   public Double getTotmisos() {
      return totmisos;
   }

   public void setTotmisos(Double totmisos) {
      this.totmisos = totmisos;
   }

   public Double getTotdeven() {
      return totdeven;
   }

   public void setTotdeven(Double totdeven) {
      this.totdeven = totdeven;
   }

   public String getFuncion() {
      return funcion;
   }

   public void setFuncion(String funcion) {
      this.funcion = funcion;
   }

   public Long getIdestrfunc() {
      return idestrfunc;
   }

   public void setIdestrfunc(Long idestrfunc) {
      this.idestrfunc = idestrfunc;
   }

   public String getCodacti() {
      return codacti;
   }

   public void setCodacti(String codacti) {
      this.codacti = codacti;
   }

   public Clasificador getIntcla() {
      return intcla;
   }

   public void setIntcla(Clasificador intcla) {
      this.intcla = intcla;
   }

   public String getCodpart() {
      return codpart;
   }

   public void setCodpart(String codpart) {
      this.codpart = codpart;
   }

   public Boolean getSwpluri() {
      return swpluri;
   }

   public void setSwpluri(Boolean swpluri) {
      this.swpluri = swpluri;
   }

   public Integer getUsucrea() {
      return usucrea;
   }

   public void setUsucrea(Integer usucrea) {
      this.usucrea = usucrea;
   }

   public ZonedDateTime getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(ZonedDateTime feccrea) {
      this.feccrea = feccrea;
   }

   public Integer getUsumodi() {
      return usumodi;
   }

   public void setUsumodi(Integer usumodi) {
      this.usumodi = usumodi;
   }

   public ZonedDateTime getFecmodi() {
      return fecmodi;
   }

   public void setFecmodi(ZonedDateTime fecmodi) {
      this.fecmodi = fecmodi;
   }

}
