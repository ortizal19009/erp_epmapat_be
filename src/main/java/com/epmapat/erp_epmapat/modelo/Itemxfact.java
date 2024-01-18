package com.epmapat.erp_epmapat.modelo;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "itemxfact")

public class Itemxfact {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long iditemxfact;
   private Long estado;
   private Float cantidad;
   private Float valorunitario;
   private Float descuento;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idfacturacion_facturacion")
   private Facturacion idfacturacion_facturacion;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idcatalogoitems_catalogoitems")
   private Catalogoitems idcatalogoitems_catalogoitems;     //Productos

   public Itemxfact() {   }
   
   public Itemxfact(Long iditemxfact, Long estado, Float cantidad, Float valorunitario,
         Facturacion idfacturacion_facturacion, Catalogoitems idcatalogoitems_catalogoitems) {
      this.iditemxfact = iditemxfact;
      this.estado = estado;
      this.cantidad = cantidad;
      this.valorunitario = valorunitario;
      this.idfacturacion_facturacion = idfacturacion_facturacion;
      this.idcatalogoitems_catalogoitems = idcatalogoitems_catalogoitems;
   }

   public Long getIditemxfact() {
      return iditemxfact;
   }

   public void setIditemxfact(Long iditemxfact) {
      this.iditemxfact = iditemxfact;
   }

   public Long getEstado() {
      return estado;
   }

   public void setEstado(Long estado) {
      this.estado = estado;
   }

   public Float getCantidad() {
      return cantidad;
   }

   public void setCantidad(Float cantidad) {
      this.cantidad = cantidad;
   }

   public Float getValorunitario() {
      return valorunitario;
   }

   public void setValorunitario(Float valorunitario) {
      this.valorunitario = valorunitario;
   }

   public Facturacion getIdfacturacion_facturacion() {
      return idfacturacion_facturacion;
   }

   public void setIdfacturacion_facturacion(Facturacion idfacturacion_facturacion) {
      this.idfacturacion_facturacion = idfacturacion_facturacion;
   }

   public Catalogoitems getIdcatalogoitems_catalogoitems() {
      return idcatalogoitems_catalogoitems;
   }

   public void setIdcatalogoitems_catalogoitems(Catalogoitems idcatalogoitems_catalogoitems) {
      this.idcatalogoitems_catalogoitems = idcatalogoitems_catalogoitems;
   }

   public Float getDescuento() {
      return descuento;
   }

   public void setDescuento(Float descuento) {
      this.descuento = descuento;
   }

}
