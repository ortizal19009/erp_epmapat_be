package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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
}
