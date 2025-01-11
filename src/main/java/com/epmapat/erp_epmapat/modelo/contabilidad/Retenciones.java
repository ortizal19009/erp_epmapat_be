package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.epmapat.erp_epmapat.modelo.administracion.Documentos;

@Entity
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

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idasiento")
   private Asientos idasiento;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idbene")
   private Beneficiarios idbene;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "intdoc")
   private Documentos intdoc;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "idautoriza")
   private Autorizaxbene idautoriza;

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

   public Retenciones() {
      super();
   }

   public Long getIdrete() {
      return idrete;
   }

   public void setIdrete(Long idrete) {
      this.idrete = idrete;
   }

   public Date getFecharegistro() {
      return fecharegistro;
   }

   public void setFecharegistro(Date fecharegistro) {
      this.fecharegistro = fecharegistro;
   }

   public String getSecretencion1() {
      return secretencion1;
   }

   public void setSecretencion1(String secretencion1) {
      this.secretencion1 = secretencion1;
   }

   public Date getFechaemision() {
      return fechaemision;
   }

   public void setFechaemision(Date fechaemision) {
      this.fechaemision = fechaemision;
   }

   public Date getFechaemiret1() {
      return fechaemiret1;
   }

   public void setFechaemiret1(Date fechaemiret1) {
      this.fechaemiret1 = fechaemiret1;
   }

   public String getNumdoc() {
      return numdoc;
   }

   public void setNumdoc(String numdoc) {
      this.numdoc = numdoc;
   }

   public BigDecimal getPorciva() {
      return porciva;
   }

   public void setPorciva(BigDecimal porciva) {
      this.porciva = porciva;
   }

   public Integer getSwretencion() {
      return swretencion;
   }

   public void setSwretencion(Integer swretencion) {
      this.swretencion = swretencion;
   }

   public BigDecimal getBaseimponible() {
      return baseimponible;
   }

   public void setBaseimponible(BigDecimal baseimponible) {
      this.baseimponible = baseimponible;
   }

   public BigDecimal getBaseimpgrav() {
      return baseimpgrav;
   }

   public void setBaseimpgrav(BigDecimal baseimpgrav) {
      this.baseimpgrav = baseimpgrav;
   }

   public BigDecimal getBasenograiva() {
      return basenograiva;
   }

   public void setBasenograiva(BigDecimal basenograiva) {
      this.basenograiva = basenograiva;
   }

   public BigDecimal getBaseimpice() {
      return baseimpice;
   }

   public void setBaseimpice(BigDecimal baseimpice) {
      this.baseimpice = baseimpice;
   }

   public BigDecimal getMontoiva() {
      return montoiva;
   }

   public void setMontoiva(BigDecimal montoiva) {
      this.montoiva = montoiva;
   }

   public Integer getPorcentajeice() {
      return porcentajeice;
   }

   public void setPorcentajeice(Integer porcentajeice) {
      this.porcentajeice = porcentajeice;
   }

   public BigDecimal getMontoice() {
      return montoice;
   }

   public void setMontoice(BigDecimal montoice) {
      this.montoice = montoice;
   }

   public BigDecimal getMontoivabienes() {
      return montoivabienes;
   }

   public void setMontoivabienes(BigDecimal montoivabienes) {
      this.montoivabienes = montoivabienes;
   }

   public String getCodretbienes() {
      return codretbienes;
   }

   public void setCodretbienes(String codretbienes) {
      this.codretbienes = codretbienes;
   }

   public Integer getPorretbienes() {
      return porretbienes;
   }

   public void setPorretbienes(Integer porretbienes) {
      this.porretbienes = porretbienes;
   }

   public BigDecimal getValorretbienes() {
      return valorretbienes;
   }

   public void setValorretbienes(BigDecimal valorretbienes) {
      this.valorretbienes = valorretbienes;
   }

   public BigDecimal getMontoivaservicios() {
      return montoivaservicios;
   }

   public void setMontoivaservicios(BigDecimal montoivaservicios) {
      this.montoivaservicios = montoivaservicios;
   }

   public String getCodretservicios() {
      return codretservicios;
   }

   public void setCodretservicios(String codretservicios) {
      this.codretservicios = codretservicios;
   }

   public BigDecimal getPorretservicios() {
      return porretservicios;
   }

   public void setPorretservicios(BigDecimal porretservicios) {
      this.porretservicios = porretservicios;
   }

   public BigDecimal getValorretservicios() {
      return valorretservicios;
   }

   public void setValorretservicios(BigDecimal valorretservicios) {
      this.valorretservicios = valorretservicios;
   }

   public BigDecimal getMontoivaserv100() {
      return montoivaserv100;
   }

   public void setMontoivaserv100(BigDecimal montoivaserv100) {
      this.montoivaserv100 = montoivaserv100;
   }

   public String getCodretserv100() {
      return codretserv100;
   }

   public void setCodretserv100(String codretserv100) {
      this.codretserv100 = codretserv100;
   }

   public BigDecimal getPorretserv100() {
      return porretserv100;
   }

   public void setPorretserv100(BigDecimal porretserv100) {
      this.porretserv100 = porretserv100;
   }

   public BigDecimal getValretserv100() {
      return valretserv100;
   }

   public void setValretserv100(BigDecimal valretserv100) {
      this.valretserv100 = valretserv100;
   }

   public BigDecimal getBaseimpair() {
      return baseimpair;
   }

   public void setBaseimpair(BigDecimal baseimpair) {
      this.baseimpair = baseimpair;
   }

   public String getCodretair() {
      return codretair;
   }

   public void setCodretair(String codretair) {
      this.codretair = codretair;
   }

   public BigDecimal getPorcentajeair() {
      return porcentajeair;
   }

   public void setPorcentajeair(BigDecimal porcentajeair) {
      this.porcentajeair = porcentajeair;
   }

   public BigDecimal getValretair() {
      return valretair;
   }

   public void setValretair(BigDecimal valretair) {
      this.valretair = valretair;
   }

   public String getNumautoriza() {
      return numautoriza;
   }

   public void setNumautoriza(String numautoriza) {
      this.numautoriza = numautoriza;
   }

   public String getNumserie() {
      return numserie;
   }

   public void setNumserie(String numserie) {
      this.numserie = numserie;
   }

   public Date getFechacaduca() {
      return fechacaduca;
   }

   public void setFechacaduca(Date fechacaduca) {
      this.fechacaduca = fechacaduca;
   }

   public String getDescripcion() {
      return descripcion;
   }

   public void setDescripcion(String descripcion) {
      this.descripcion = descripcion;
   }

   public Asientos getIdasiento() {
      return idasiento;
   }

   public void setIdasiento(Asientos idasiento) {
      this.idasiento = idasiento;
   }

   public Beneficiarios getIdbene() {
      return idbene;
   }

   public void setIdbene(Beneficiarios idbene) {
      this.idbene = idbene;
   }

   public Documentos getIntdoc() {
      return intdoc;
   }

   public void setIntdoc(Documentos intdoc) {
      this.intdoc = intdoc;
   }

   public Autorizaxbene getIdautoriza() {
      return idautoriza;
   }

   public void setIdautoriza(Autorizaxbene idautoriza) {
      this.idautoriza = idautoriza;
   }

   public Tabla01 getIdtabla01() {
      return idtabla01;
   }

   public void setIdtabla01(Tabla01 idtabla01) {
      this.idtabla01 = idtabla01;
   }

   public Tabla15 getIdtabla15() {
      return idtabla15;
   }

   public void setIdtabla15(Tabla15 idtabla15) {
      this.idtabla15 = idtabla15;
   }

   public Integer getIdtabla5c_100() {
      return idtabla5c_100;
   }

   public void setIdtabla5c_100(Integer idtabla5c_100) {
      this.idtabla5c_100 = idtabla5c_100;
   }

   public Integer getIdtabla5c_bie() {
      return idtabla5c_bie;
   }

   public void setIdtabla5c_bie(Integer idtabla5c_bie) {
      this.idtabla5c_bie = idtabla5c_bie;
   }

   public Integer getIdtabla5c_ser() {
      return idtabla5c_ser;
   }

   public void setIdtabla5c_ser(Integer idtabla5c_ser) {
      this.idtabla5c_ser = idtabla5c_ser;
   }

   public String getClaveacceso() {
      return claveacceso;
   }

   public void setClaveacceso(String claveacceso) {
      this.claveacceso = claveacceso;
   }

   public String getNumautoriza_e() {
      return numautoriza_e;
   }

   public void setNumautoriza_e(String numautoriza_e) {
      this.numautoriza_e = numautoriza_e;
   }

   public String getFecautoriza() {
      return fecautoriza;
   }

   public void setFecautoriza(String fecautoriza) {
      this.fecautoriza = fecautoriza;
   }

   public Integer getEstado() {
      return estado;
   }

   public void setEstado(Integer estado) {
      this.estado = estado;
   }

   public String getAmbiente() {
      return ambiente;
   }

   public void setAmbiente(String ambiente) {
      this.ambiente = ambiente;
   }

   public String getAutorizacion() {
      return autorizacion;
   }

   public void setAutorizacion(String autorizacion) {
      this.autorizacion = autorizacion;
   }

   public Integer getSwelectro() {
      return swelectro;
   }

   public void setSwelectro(Integer swelectro) {
      this.swelectro = swelectro;
   }

   public Tabla17 getIdtabla17() {
      return idtabla17;
   }

   public void setIdtabla17(Tabla17 idtabla17) {
      this.idtabla17 = idtabla17;
   }

   public Integer getEscoge() {
      return escoge;
   }

   public void setEscoge(Integer escoge) {
      this.escoge = escoge;
   }

   public Integer getInttra() {
      return inttra;
   }

   public void setInttra(Integer inttra) {
      this.inttra = inttra;
   }

}