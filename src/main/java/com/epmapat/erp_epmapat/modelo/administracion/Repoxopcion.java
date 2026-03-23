package com.epmapat.erp_epmapat.modelo.administracion;

import java.sql.Timestamp;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Entity
@Table(name = "repoxopcion")
@Data
public class Repoxopcion {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private short idrepoxopcion;

   private String codigo;
   private String opcion;
   private String nombre;

   private short usucrea;
   private Timestamp feccrea;
   private Short usumodi;
   private Timestamp fecmodi;

   @PrePersist
   protected void onCreate() {
      feccrea = new Timestamp(System.currentTimeMillis());
   }

   @PreUpdate
   protected void onUpdate() {
      fecmodi = new Timestamp(System.currentTimeMillis());
   }

   // Constructor adicional para inicializar solo idrepoxopcion
   public Repoxopcion(Short idrepoxopcion) {
      this.idrepoxopcion = idrepoxopcion;
   }
   
}
