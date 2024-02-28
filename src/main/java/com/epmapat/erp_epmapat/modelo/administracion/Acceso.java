package com.epmapat.erp_epmapat.modelo.administracion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "acceso")

public class Acceso {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idacc;
   private String codacc;
   private String nomacc;
   private Integer regacc;

   public Long getIdacc() {
      return idacc;
   }
   public void setIdacc(Long idacc) {
      this.idacc = idacc;
   }
   public String getCodacc() {
      return codacc;
   }
   public void setCodacc(String codacc) {
      this.codacc = codacc;
   }
   public String getNomacc() {
      return nomacc;
   }
   public void setNomacc(String nomacc) {
      this.nomacc = nomacc;
   }
   public Integer getRegacc() {
      return regacc;
   }
   public void setRegacc(Integer regacc) {
      this.regacc = regacc;
   }
   
}
