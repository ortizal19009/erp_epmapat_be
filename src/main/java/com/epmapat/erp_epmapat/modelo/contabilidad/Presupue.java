package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

import javax.persistence.*;

import com.epmapat.erp_epmapat.modelo.Clasificador;

import lombok.Data;

// import com.epmapat.erp_epmapat.modelo.Clasificador;

@Entity
@Data
@Table(name = "presupue")

public class Presupue {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long intpre;
   
   private Integer tippar;
   private String codpar;
   private String codigo;
   private String nompar;
   private Double inicia;
   private Double totmod;
   private BigDecimal totcerti;
   private BigDecimal totmisos;
   private Double totdeven;
   private String funcion;

   // private Long intest;
   // @ManyToOne(fetch = FetchType.LAZY)
   private String codacti;

   //Para el clasificador usa intcla y codpart
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intcla")
   private Clasificador intcla;
   private String codpart;

   private Integer swpluri;

   private Integer usucrea;
   @Column(name = "feccrea")
   private ZonedDateTime feccrea;

   private Integer usumodi;
   @Column(name = "fecmodi")
   private ZonedDateTime fecmodi;
}
