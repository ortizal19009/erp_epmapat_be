package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "niifcuentas")

public class NiifCuentas {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idniifcue;
   private String codcue;
   private String nomcue;
   private String grucue;
   private Long nivcue;
   private Boolean movcue;

   private Long usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;

   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;

   public NiifCuentas(Long idniifcue, String codcue, String nomcue, String grucue, Long nivcue, Boolean movcue,
         Long usucrea, ZonedDateTime feccrea, Long usumodi, ZonedDateTime fecmodi) {
      super();
      this.idniifcue = idniifcue;
      this.codcue = codcue;
      this.nomcue = nomcue;
      this.grucue = grucue;
      this.nivcue = nivcue;
      this.movcue = movcue;
      this.usucrea = usucrea;
      this.feccrea = feccrea;
      this.usumodi = usumodi;
      this.fecmodi = fecmodi;
   }

   public NiifCuentas() {
      super();
    
   }

   public Long getIdniifcue() {
      return idniifcue;
   }

   public void setIdniifcue(Long idniifcue) {
      this.idniifcue = idniifcue;
   }

   public String getCodcue() {
      return codcue;
   }

   public void setCodcue(String codcue) {
      this.codcue = codcue;
   }

   public String getNomcue() {
      return nomcue;
   }

   public void setNomcue(String nomcue) {
      this.nomcue = nomcue;
   }

   public String getGrucue() {
      return grucue;
   }

   public void setGrucue(String grucue) {
      this.grucue = grucue;
   }

   public Long getNivcue() {
      return nivcue;
   }

   public void setNivcue(Long nivcue) {
      this.nivcue = nivcue;
   }

   public Boolean getMovcue() {
      return movcue;
   }

   public void setMovcue(Boolean movcue) {
      this.movcue = movcue;
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

}
