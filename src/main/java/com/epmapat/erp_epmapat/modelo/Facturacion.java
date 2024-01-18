package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
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
@Table(name = "facturacion")
public class Facturacion implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idfacturacion;
   Integer estado;
   String descripcion;
   Integer formapago;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idcliente_clientes")
   private Clientes idcliente_clientes;
   Float total;
   Short cuotas;
   Long usucrea;
   Date feccrea;
   Long usumodi;
   Date fecmodi;

   public Facturacion() {
      super();
   }


   public Facturacion(Long idfacturacion, Integer estado, String descripcion, Integer formapago,
         Clientes idcliente_clientes, Float total, Short cuotas, Long usucrea, Date feccrea, Long usumodi,
         Date fecmodi) {
      this.idfacturacion = idfacturacion;
      this.estado = estado;
      this.descripcion = descripcion;
      this.formapago = formapago;
      this.idcliente_clientes = idcliente_clientes;
      this.total = total;
      this.cuotas = cuotas;
      this.usucrea = usucrea;
      this.feccrea = feccrea;
      this.usumodi = usumodi;
      this.fecmodi = fecmodi;
   }

   public Long getIdfacturacion() {
      return idfacturacion;
   }

   public void setIdfacturacion(Long idfacturacion) {
      this.idfacturacion = idfacturacion;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }

   public Integer getFormapago() {
      return formapago;
   }

   public void setFormapago(Integer formapago) {
      this.formapago = formapago;
   }

   public Clientes getIdcliente_clientes() {
      return idcliente_clientes;
   }

   public void setIdcliente_clientes(Clientes idcliente_clientes) {
      this.idcliente_clientes = idcliente_clientes;
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

   public Float getTotal() {
      return total;
   }
   public void setTotal(Float total) {
      this.total = total;
   }

   public Short getCuotas() {
      return cuotas;
   }
   public void setCuotas(Short cuotas) {
      this.cuotas = cuotas;
   }

}
