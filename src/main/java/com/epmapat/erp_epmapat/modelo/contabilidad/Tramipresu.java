package com.epmapat.erp_epmapat.modelo.contabilidad;

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
@Table(name = "tramipresu")
public class Tramipresu {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idtrami;
   private Long numero;
   @Column(name = "fecha")
   private ZonedDateTime fecha;
   private String numdoc;
   @Column(name = "fecdoc")
   private ZonedDateTime fecdoc;
   private Long totmiso;
   private String descripcion;
   private Long usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;
   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;
   private Boolean swreinte;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   private Beneficiarios idbene;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "iddocumento")
   private Documentos iddocumento;
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
   public ZonedDateTime getFecha() {
      return fecha;
   }
   public void setFecha(ZonedDateTime fecha) {
      this.fecha = fecha;
   }
   public String getNumdoc() {
      return numdoc;
   }
   public void setNumdoc(String numdoc) {
      this.numdoc = numdoc;
   }
   public ZonedDateTime getFecdoc() {
      return fecdoc;
   }
   public void setFecdoc(ZonedDateTime fecdoc) {
      this.fecdoc = fecdoc;
   }
   public Long getTotmiso() {
      return totmiso;
   }
   public void setTotmiso(Long totmiso) {
      this.totmiso = totmiso;
   }
   public String getDescripcion() {
      return descripcion;
   }
   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
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
   public Boolean getSwreinte() {
      return swreinte;
   }
   public void setSwreinte(Boolean swreinte) {
      this.swreinte = swreinte;
   }
   public Beneficiarios getIdbene() {
      return idbene;
   }
   public void setIdbene(Beneficiarios idbene) {
      this.idbene = idbene;
   }
   public Documentos getIddocumento() {
      return iddocumento;
   }
   public void setIddocumento(Documentos iddocumento) {
      this.iddocumento = iddocumento;
   }

   
}
