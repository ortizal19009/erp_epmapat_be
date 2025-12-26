package com.epmapat.erp_epmapat.modelo.administracion;

import java.time.LocalDate;
import java.time.ZonedDateTime;

import javax.persistence.Column;
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
@Table(name = "usuarios")

public class Usuarios {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idusuario;
   private String identificausu;
   private String codusu;
   private String nomusu;
   private String email;
   private Boolean estado;
   @Column(name = "fdesde")
   private LocalDate fdesde;
   @Column(name = "fhasta")
   private LocalDate fhasta;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;
   private Long usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;
   private Boolean otrapestania;
   private String alias;
   private String priusu;
   private String perfil;
   private Long toolbarframe;
   private Long toolbarsheet;
   private String plataform_access;

}