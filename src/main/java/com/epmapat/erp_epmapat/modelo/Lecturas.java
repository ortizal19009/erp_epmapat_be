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
@Table(name = "lecturas")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Lecturas implements Serializable {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idlectura;
   Integer estado;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idrutaxemision_rutasxemision")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Rutasxemision idrutaxemision_rutasxemision;
   Date fechaemision;
   Float lecturaanterior;
   Float lecturaactual;
   Float lecturadigitada;
   Integer mesesmulta;
   String observaciones;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idnovedad_novedades")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Novedad idnovedad_novedades;
   Long idemision;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idabonado_abonados")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Abonados idabonado_abonados;
   Long idresponsable;
   Long idcategoria;
   Long idfactura;
   private BigDecimal total1;
   private BigDecimal total31;
   private BigDecimal total32;
   @Transient
   private String fotoPath;

}
