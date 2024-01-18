package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "estrfunc")

public class Estrfunc implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)

   private Long idestrfunc;
   String codigo;
   String nombre;
   Integer objcosto;
   Boolean movimiento;

   public Long getIdestrfunc() {
      return idestrfunc;
   }
   public void setIdestrfunc(Long idestrfunc) {
      this.idestrfunc = idestrfunc;
   }
   public String getCodigo() {
      return codigo;
   }
   public void setCodigo(String codigo) {
      this.codigo = codigo;
   }
   public String getNombre() {
      return nombre;
   }
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   public Integer getObjcosto() {
      return objcosto;
   }
   public void setObjcosto(Integer objcosto) {
      this.objcosto = objcosto;
   }
   public Boolean getMovimiento() {
      return movimiento;
   }
   public void setMovimiento(Boolean movimiento) {
      this.movimiento = movimiento;
   }
   
}
