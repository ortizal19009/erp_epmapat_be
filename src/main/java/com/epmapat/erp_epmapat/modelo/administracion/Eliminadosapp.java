package com.epmapat.erp_epmapat.modelo.administracion;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Data
@Table(name = "eliminadosapp")

public class Eliminadosapp {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long ideliminado;

   private short idusuario;
   private short modulo;
   private Timestamp fecha;
   private String routerlink;
   private String tabla;
   private String datos;

}