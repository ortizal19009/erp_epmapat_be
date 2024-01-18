package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name="tipotramite")

public class TipoTramite {
   
   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idtipotramite;
   private String descripcion;
   private Integer facturable;
   private Long usucrea; 
   private Date feccrea;
	private Long usumodi;
   private Date fecmodi;

   public Long getIdtipotramite() {
      return idtipotramite;
   }
   public void setIdtipotramite(Long idtipotramite) {
      this.idtipotramite = idtipotramite;
   }

   public String getDescripcion() {
      return descripcion;
   }
   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }
   
   public Integer getFacturable() {
      return facturable;
   }
   public void setFacturable(Integer facturable) {
      this.facturable = facturable;
   }
   public Long getUsucrea() {
      return usucrea;
   }
   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }
   public Date getFeccrea() {
      return feccrea;
   }
   public void setFeccrea(Date feccrea) {
      this.feccrea = feccrea;
   }
   public Long getUsumodi() {
      return usumodi;
   }
   public void setUsumodi(Long usumodi) {
      this.usumodi = usumodi;
   }
   public Date getFecmodi() {
      return fecmodi;
   }
   public void setFecmodi(Date fecmodi) {
      this.fecmodi = fecmodi;
   }
   
   
}
