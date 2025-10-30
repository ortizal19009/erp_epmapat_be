package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "catalogoitems")

public class Catalogoitems {
   
   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idcatalogoitems;
   private String descripcion;
   private Float cantidad;
   private Integer facturable;
   private Boolean estado;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idusoitems_usoitems")
	private Usoitems idusoitems_usoitems;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="idrubro_rubros")
	private Rubros idrubro_rubros;
   private Long usucrea;
   private Date feccrea;
   private Long usumodi;
   private Date fecmodi;

   public Long getIdcatalogoitems() {
      return idcatalogoitems;
   }

   public void setIdcatalogoitems(Long idcatalogoitems) {
      this.idcatalogoitems = idcatalogoitems;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }
   
   public Float getCantidad() {
      return cantidad;
   }
   public void setCantidad(Float cantidad) {
      this.cantidad = cantidad;
   }
   public Integer getFacturable() {
      return facturable;
   }
   public void setFacturable(Integer facturable) {
      this.facturable = facturable;
   }
   public Boolean getEstado() {
      return estado;
   }
   public void setEstado(Boolean estado) {
      this.estado = estado;
   }

   public Usoitems getIdusoitems_usoitems() {
      return idusoitems_usoitems;
   }
   public void setIdusoitems_usoitems(Usoitems idusoitems_usoitems) {
      this.idusoitems_usoitems = idusoitems_usoitems;
   }

   public Rubros getIdrubro_rubros() {
      return idrubro_rubros;
   }
   public void setIdrubro_rubros(Rubros idrubro_rubros) {
      this.idrubro_rubros = idrubro_rubros;
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
