package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "retenciones")

public class Retenciones {

   @Id
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private Long idrete;
   @Temporal(TemporalType.DATE)
   @DateTimeFormat(iso = ISO.DATE)
   @Column(name = "fecharegistro")
   private Date fecharegistro;

   private String secretencion1;
   @Temporal(TemporalType.DATE)
   @DateTimeFormat(iso = ISO.DATE)
   @Column(name = "fechaemision")
   private Date fechaemision;

   @Temporal(TemporalType.DATE)
   @DateTimeFormat(iso = ISO.DATE)
   @Column(name = "fechaemiret1")
   private Date fechaemiret1;

   private String numdoc;
   private BigDecimal porciva;
   private Integer swretencion;
   private BigDecimal baseimponible;
   private BigDecimal baseimpgrav;
   private BigDecimal basenograiva;
   private BigDecimal baseimpice;
   private BigDecimal montoiva;
   private Integer porcentajeice;
   private BigDecimal montoice;
   private BigDecimal montoivabienes;
   private String codretbienes;
   private Integer porretbienes;
   private BigDecimal valorretbienes;
   private BigDecimal montoivaservicios;
   private String codretservicios;
   private BigDecimal porretservicios;
   private BigDecimal valorretservicios;
   private BigDecimal montoivaserv100;
   private String codretserv100;
   private BigDecimal porretserv100;
   private BigDecimal valretserv100;
   private BigDecimal baseimpair;
   private String codretair;
   private BigDecimal porcentajeair;
   private BigDecimal valretair;
   private String numautoriza;
   private String numserie;
   @Temporal(TemporalType.DATE)
   @DateTimeFormat(iso = ISO.DATE)
   @Column(name = "fechacaduca")
   private Date fechacaduca;
   private String descripcion;

   @OneToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idasiento")
   private Asientos idasiento;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   private Beneficiarios idbene;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "iddocu")
   private Documentos iddocu;

   // @ManyToOne(fetch = FetchType.LAZY)
   // @JoinColumn(name = "idautoriza")
   // private Autorizaxbene idautoriza;
   private Integer idautoriza;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idtabla01")
   private Tabla01 idtabla01;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idtabla15")
   private Tabla15 idtabla15;

   private Integer idtabla5c_100;
   private Integer idtabla5c_bie;
   private Integer idtabla5c_ser;

   private String claveacceso;
   private String numautoriza_e;
   private String fecautoriza;
   private Integer estado;
   private String ambiente;
   private String autorizacion;
   private Integer swelectro;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idtabla17")
   private Tabla17 idtabla17;

   private Integer escoge;
   private Integer inttra;

}