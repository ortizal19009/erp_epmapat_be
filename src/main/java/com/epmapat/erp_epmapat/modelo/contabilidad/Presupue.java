package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.Clasificador;

import lombok.Data;

// import com.epmapat.erp_epmapat.modelo.Clasificador;

@Entity
@Data
@Table(name = "presupue")

public class Presupue {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long intpre;
   
   private Integer tippar;
   private String codpar;
   private String codigo;
   private String nompar;
   private Double inicia;
   private Double totmod;
   private BigDecimal totcerti;
   private Double totmisos;
   private Double totdeven;
   private String funcion;

   // private Long intest;
   // @ManyToOne(fetch = FetchType.LAZY)
   private String codacti;

   //Para el clasificador usa intcla y codpart
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intcla")
   private Clasificador intcla;
   private String codpart;

   private Integer swpluri;

   private Integer usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;

   private Integer usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;

   // public Long getIntpre() {
   //    return intpre;
   // }

   // public void setIntpre(Long intpre) {
   //    this.intpre = intpre;
   // }

   // public Integer getTippar() {
   //    return tippar;
   // }

   // public void setTippar(Integer tippar) {
   //    this.tippar = tippar;
   // }

   // public String getCodpar() {
   //    return codpar;
   // }

   // public void setCodpar(String codpar) {
   //    this.codpar = codpar;
   // }

   // public String getCodigo() {
   //    return codigo;
   // }

   // public void setCodigo(String codigo) {
   //    this.codigo = codigo;
   // }

   // public String getNompar() {
   //    return nompar;
   // }

   // public void setNompar(String nompar) {
   //    this.nompar = nompar;
   // }

   // public Double getInicia() {
   //    return inicia;
   // }

   // public void setInicia(Double inicia) {
   //    this.inicia = inicia;
   // }

   // public Double getTotmod() {
   //    return totmod;
   // }

   // public void setTotmod(Double totmod) {
   //    this.totmod = totmod;
   // }

   // public BigDecimal getTotcerti() {
   //    return totcerti;
   // }

   // public void setTotcerti(BigDecimal totcerti) {
   //    this.totcerti = totcerti;
   // }

   // public Double getTotmisos() {
   //    return totmisos;
   // }

   // public void setTotmisos(Double totmisos) {
   //    this.totmisos = totmisos;
   // }

   // public Double getTotdeven() {
   //    return totdeven;
   // }

   // public void setTotdeven(Double totdeven) {
   //    this.totdeven = totdeven;
   // }

   // public String getFuncion() {
   //    return funcion;
   // }

   // public void setFuncion(String funcion) {
   //    this.funcion = funcion;
   // }

   // // public Long getIntest() {
   // //    return intest;
   // // }
   // // public void setIntest(Long intest) {
   // //    this.intest = intest;
   // // }

   // public String getCodacti() {
   //    return codacti;
   // }

   // public void setCodacti(String codacti) {
   //    this.codacti = codacti;
   // }

   // //Para el clasificador usa intcla y codpart
   // public Clasificador getIntcla() {
   //    return intcla;
   // }

   // public void setIntcla(Clasificador intcla) {
   //    this.intcla = intcla;
   // }

   // public String getCodpart() {
   //    return codpart;
   // }

   // public void setCodpart(String codpart) {
   //    this.codpart = codpart;
   // }

   // public Integer getSwpluri() {
   //    return swpluri;
   // }

   // public void setSwpluri(Integer swpluri) {
   //    this.swpluri = swpluri;
   // }

   // public Integer getUsucrea() {
   //    return usucrea;
   // }

   // public void setUsucrea(Integer usucrea) {
   //    this.usucrea = usucrea;
   // }

   // public ZonedDateTime getFeccrea() {
   //    return feccrea;
   // }

   // public void setFeccrea(ZonedDateTime feccrea) {
   //    this.feccrea = feccrea;
   // }

   // public Integer getUsumodi() {
   //    return usumodi;
   // }

   // public void setUsumodi(Integer usumodi) {
   //    this.usumodi = usumodi;
   // }

   // public ZonedDateTime getFecmodi() {
   //    return fecmodi;
   // }

   // public void setFecmodi(ZonedDateTime fecmodi) {
   //    this.fecmodi = fecmodi;
   // }

}
