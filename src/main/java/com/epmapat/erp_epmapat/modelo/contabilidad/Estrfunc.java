package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;

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
   private Long intest;

   String codigo;
   String nombre;
   String funcion;
   Integer movimiento;
   Integer objcosto;
   BigDecimal b1;
   BigDecimal b2;
   BigDecimal b3;
   BigDecimal b4;
   String c1;
   Integer i1;

   public Long getIntest() {
      return intest;
   }
   public void setIntest(Long intest) {
      this.intest = intest;
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
   public Integer getMovimiento() {
      return movimiento;
   }
   public void setMovimiento(Integer movimiento) {
      this.movimiento = movimiento;
   }

   public String getFuncion() {
      return funcion;
   }
   public void setFuncion(String funcion) {
      this.funcion = funcion;
   }
   public BigDecimal getB1() {
      return b1;
   }
   public void setB1(BigDecimal b1) {
      this.b1 = b1;
   }
   public BigDecimal getB2() {
      return b2;
   }
   public void setB2(BigDecimal b2) {
      this.b2 = b2;
   }
   public BigDecimal getB3() {
      return b3;
   }
   public void setB3(BigDecimal b3) {
      this.b3 = b3;
   }
   public BigDecimal getB4() {
      return b4;
   }
   public void setB4(BigDecimal b4) {
      this.b4 = b4;
   }
   public String getC1() {
      return c1;
   }
   public void setC1(String c1) {
      this.c1 = c1;
   }
   public Integer getI1() {
      return i1;
   }
   public void setI1(Integer i1) {
      this.i1 = i1;
   }

}
