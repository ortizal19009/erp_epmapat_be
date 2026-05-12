package com.epmapat.erp_epmapat.modelo.administracion;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import org.hibernate.annotations.TypeDef;

import com.fasterxml.jackson.databind.JsonNode;
import com.vladmihalcea.hibernate.type.json.JsonType;
import org.hibernate.annotations.Type;

import lombok.Data;

@Entity
@Data
@TypeDef(name = "json", typeClass = JsonType.class)
@Table(name = "reportesjr")

public class Reportesjr {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Short idreporte;

   @Column(nullable = false, length = 50, unique = true)
   private String nomrep;

   @Column(nullable = false, length = 100)
   private String desrep;

   @Column(name = "jrxml", nullable = false)
   private byte[] jrxml;

   @Column(name = "jasper", nullable = false)
   private byte[] jasper;

   // @Type(JsonType.class)
   // @Column(columnDefinition = "json")
   // private JsonNode parametros;

   @Type(type = "json")
   @Column(columnDefinition = "json")
   private JsonNode parametros;

   private short metodo; // 1: SQL Directo, 2: Coleccion de Beans, 3: Dataset desde frontend

   @Column(nullable = false, updatable = false)
   private Timestamp feccrea;

   private Timestamp fecmodi;

   @PrePersist
   protected void onCreate() {
      feccrea = new Timestamp(System.currentTimeMillis());
   }

   @PreUpdate
   protected void onUpdate() {
      fecmodi = new Timestamp(System.currentTimeMillis());
   }

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idrepoxopcion")
   private Repoxopcion repoxopcion;

}
