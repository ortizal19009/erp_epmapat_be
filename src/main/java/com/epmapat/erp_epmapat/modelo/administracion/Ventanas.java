package com.epmapat.erp_epmapat.modelo.administracion;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ventanas")

public class Ventanas {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idventana;

   private String nombre;
   private String color1;
   private String color2;
   private Long idusuario;
   
   public Long getIdventana() {
      return idventana;
   }
   public void setIdventana(Long idventana) {
      this.idventana = idventana;
   }
   public String getNombre() {
      return nombre;
   }
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   public String getColor1() {
      return color1;
   }
   public void setColor1(String color1) {
      this.color1 = color1;
   }
   public String getColor2() {
      return color2;
   }
   public void setColor2(String color2) {
      this.color2 = color2;
   }
   public Long getIdusuario() {
      return idusuario;
   }
   public void setIdusuario(Long idusuario) {
      this.idusuario = idusuario;
   }

}
