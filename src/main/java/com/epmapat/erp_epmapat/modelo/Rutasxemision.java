package com.epmapat.erp_epmapat.modelo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "rutasxemision")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})

public class Rutasxemision implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idrutaxemision;
   Integer estado;
   Long usuariocierre;
   Date fechacierre;
   Long usucrea;
   Date feccrea;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idemision_emisiones")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Emisiones idemision_emisiones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idruta_rutas")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Rutas idruta_rutas;
   private Long m3;
   private BigDecimal total;
   @Transient
   private Long totalLecturas;
   @Transient
   private Long lecturasCargadas;

}
