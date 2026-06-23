package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Entity
@Data
@Table(name = "tramites")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Tramipresu {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idtrami;
   private Long numero;
   private LocalDate fecha;
   private String numdoc;
   private LocalDate fecdoc;
   private BigDecimal totmiso;
   private String descri;
   private Integer swreinte;
   private Long usucrea;
   private Date feccrea;
   private Long usumodi;
   private Date fecmodi;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Beneficiarios idbene;
   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intdoc")
   @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
   private Documentos intdoc;

}
