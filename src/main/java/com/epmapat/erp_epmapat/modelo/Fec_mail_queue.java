package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
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
@Table(name = "fec_mail_queue")
public class Fec_mail_queue implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long id;

   private Long idfactura;
   private String correo;
   private String estado;
   private Integer intentos;
   private String ultimoError;
   private LocalDateTime fechaCrea;
   private LocalDateTime fechaEnvio;
   private Long usuarioSolicita;
   private Integer prioridad;
   private String correlationId;
   private String ipSolicita;

   @PrePersist
   void onCreate() {
      if (fechaCrea == null) {
         fechaCrea = LocalDateTime.now();
      }
      if (estado == null || estado.isBlank()) {
         estado = "PENDIENTE";
      }
      if (intentos == null) {
         intentos = 0;
      }
      if (prioridad == null) {
         prioridad = 1;
      }
   }
}
