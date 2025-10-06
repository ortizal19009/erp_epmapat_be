package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
@Table(name = "tramites")
public class Tramipresu {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idtrami;
   private Long numero;
   // @Column(name = "fecha")
   private LocalDate fecha;
   private String numdoc;
   // @Column(name = "fecdoc")
   private LocalDate fecdoc;
   private Long totmiso;
   private String descri;
   private Long usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;
   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;
   private Integer swreinte;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   private Beneficiarios idbene;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intdoc")
   private Documentos intdoc;
   public Long getIdtrami() {
      return idtrami;
   }
   public void setIdtrami(Long idtrami) {
      this.idtrami = idtrami;
   }
   public Long getNumero() {
      return numero;
   }
   public void setNumero(Long numero) {
      this.numero = numero;
   }
   public LocalDate getFecha() {
      return fecha;
   }
   public void setFecha(LocalDate fecha) {
      this.fecha = fecha;
   }
   public String getNumdoc() {
      return numdoc;
   }
   public void setNumdoc(String numdoc) {
      this.numdoc = numdoc;
   }
   public LocalDate getFecdoc() {
      return fecdoc;
   }
   public void setFecdoc(LocalDate fecdoc) {
      this.fecdoc = fecdoc;
   }
   public Long getTotmiso() {
      return totmiso;
   }
   public void setTotmiso(Long totmiso) {
      this.totmiso = totmiso;
   }
   public String getDescri() {
      return descri;
   }
   public void setDescri(String descri) {
      this.descri = descri;
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
   public Long getUsumodi() {
      return usumodi;
   }
   public void setUsumodi(Long usumodi) {
      this.usumodi = usumodi;
   }
   public ZonedDateTime getFecmodi() {
      return fecmodi;
   }
   public void setFecmodi(ZonedDateTime fecmodi) {
      this.fecmodi = fecmodi;
   }
   public Integer getSwreinte() {
      return swreinte;
   }
   public void setSwreinte(Integer swreinte) {
      this.swreinte = swreinte;
   }
   public Beneficiarios getIdbene() {
      return idbene;
   }
   public void setIdbene(Beneficiarios idbene) {
      this.idbene = idbene;
   }
   public Documentos getIntdoc() {
      return intdoc;
   }
   public void setIntdoc(Documentos intdoc) {
      this.intdoc = intdoc;
   }
   
}
