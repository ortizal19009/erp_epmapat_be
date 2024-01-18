package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.sql.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "clasificador")

public class Clasificador implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long intcla;
   String codpar;
   Integer nivpar;
   String grupar;
   String nompar;
   String despar;
   String cueejepresu;
   Integer presupuesto;
   Integer ejecucion;
   Integer devengado;
   Integer reforma;
   Integer asigna_ini;
   String usucrea;
   Date feccrea;
   String usumodi;
   Date fecmodi;
   String grupo;
   Integer balancostos;

   public Clasificador() {
   }

   public Clasificador(Long intcla, String codpar, Integer nivpar, String grupar, String nompar, String despar,
         String cueejepresu, Integer presupuesto, Integer ejecucion, Integer devengado, Integer reforma,
         Integer asigna_ini, String usucrea, Date feccrea, String usumodi, Date fecmodi, String grupo,
         Integer balancostos) {
      this.intcla = intcla;
      this.codpar = codpar;
      this.nivpar = nivpar;
      this.grupar = grupar;
      this.nompar = nompar;
      this.despar = despar;
      this.cueejepresu = cueejepresu;
      this.presupuesto = presupuesto;
      this.ejecucion = ejecucion;
      this.devengado = devengado;
      this.reforma = reforma;
      this.asigna_ini = asigna_ini;
      this.usucrea = usucrea;
      this.feccrea = feccrea;
      this.usumodi = usumodi;
      this.fecmodi = fecmodi;
      this.grupo = grupo;
      this.balancostos = balancostos;
   }

   public Long getIntcla() {
      return intcla;
   }

   public void setIntcla(Long intcla) {
      this.intcla = intcla;
   }

   public String getCodpar() {
      return codpar;
   }

   public void setCodpar(String codpar) {
      this.codpar = codpar;
   }

   public Integer getNivpar() {
      return nivpar;
   }

   public void setNivpar(Integer nivpar) {
      this.nivpar = nivpar;
   }

   public String getGrupar() {
      return grupar;
   }

   public void setGrupar(String grupar) {
      this.grupar = grupar;
   }

   public String getNompar() {
      return nompar;
   }

   public void setNompar(String nompar) {
      this.nompar = nompar;
   }

   public String getDespar() {
      return despar;
   }

   public void setDespar(String despar) {
      this.despar = despar;
   }

   public String getCueejepresu() {
      return cueejepresu;
   }

   public void setCueejepresu(String cueejepresu) {
      this.cueejepresu = cueejepresu;
   }

   public Integer getPresupuesto() {
      return presupuesto;
   }

   public void setPresupuesto(Integer presupuesto) {
      this.presupuesto = presupuesto;
   }

   public Integer getEjecucion() {
      return ejecucion;
   }

   public void setEjecucion(Integer ejecucion) {
      this.ejecucion = ejecucion;
   }

   public Integer getDevengado() {
      return devengado;
   }

   public void setDevengado(Integer devengado) {
      this.devengado = devengado;
   }

   public Integer getReforma() {
      return reforma;
   }

   public void setReforma(Integer reforma) {
      this.reforma = reforma;
   }

   public Integer getAsigna_ini() {
      return asigna_ini;
   }

   public void setAsigna_ini(Integer asigna_ini) {
      this.asigna_ini = asigna_ini;
   }

   public String getUsucrea() {
      return usucrea;
   }

   public void setUsucrea(String usucrea) {
      this.usucrea = usucrea;
   }

   public Date getFeccrea() {
      return feccrea;
   }

   public void setFeccrea(Date feccrea) {
      this.feccrea = feccrea;
   }

   public String getUsumodi() {
      return usumodi;
   }

   public void setUsumodi(String usumodi) {
      this.usumodi = usumodi;
   }

   public Date getFecmodi() {
      return fecmodi;
   }

   public void setFecmodi(Date fecmodi) {
      this.fecmodi = fecmodi;
   }

   public String getGrupo() {
      return grupo;
   }

   public void setGrupo(String grupo) {
      this.grupo = grupo;
   }

   public Integer getBalancostos() {
      return balancostos;
   }

   public void setBalancostos(Integer balancostos) {
      this.balancostos = balancostos;
   }

}
