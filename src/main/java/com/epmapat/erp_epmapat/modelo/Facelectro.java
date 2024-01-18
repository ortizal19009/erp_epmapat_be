package com.epmapat.erp_epmapat.modelo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "facelectro")

public class Facelectro {

   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idfacelectro;
   private String codigonumerico;
   private String digitoverificador;
   private String nombrexml;
   private Integer estado;
   private String claveacceso;
   private Integer swenviada;
   private String identificacion;
   private String nombre;
   private String direccion;
   private String telefono;
   private String concepto;
   private Float base0;
   private Float baseimponiva;
   private Float iva12;
   private Float iva0;
   private Float total;
   private String numautorizacion;
   private String mensaje;
   private String tpidentifica;
   private Float impuesto;
   private Float impuestoretener;   
   private Float porciva;
   private Long usucrea;
	private Date feccrea;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idfactura_facturas")

	private Facturas idfactura_facturas;
   
   // @ManyToOne(fetch = FetchType.LAZY)
	// @JoinColumn(name = "idcaja_cajas")
	// private Cajas idcaja_cajas;
   private Long idcaja_cajas;
   private String nrofac;
   
   public Facelectro() {
   }
   
   public Facelectro(Long idfacelectro, String codigonumerico, String digitoverificador, String nombrexml,
         Integer estado, String claveacceso, Integer swenviada, String identificacion, String nombre, String direccion,
         String telefono, String concepto, Float base0, Float baseimponiva, Float iva12, Float iva0, Float total,
         String numautorizacion, String mensaje, String tpidentifica, Float impuesto, Float impuestoretener,
         Float porciva, Long usucrea, Date feccrea, Facturas idfactura_facturas, Long idcaja_cajas, String nrofac) {
      this.idfacelectro = idfacelectro;
      this.codigonumerico = codigonumerico;
      this.digitoverificador = digitoverificador;
      this.nombrexml = nombrexml;
      this.estado = estado;
      this.claveacceso = claveacceso;
      this.swenviada = swenviada;
      this.identificacion = identificacion;
      this.nombre = nombre;
      this.direccion = direccion;
      this.telefono = telefono;
      this.concepto = concepto;
      this.base0 = base0;
      this.baseimponiva = baseimponiva;
      this.iva12 = iva12;
      this.iva0 = iva0;
      this.total = total;
      this.numautorizacion = numautorizacion;
      this.mensaje = mensaje;
      this.tpidentifica = tpidentifica;
      this.impuesto = impuesto;
      this.impuestoretener = impuestoretener;
      this.porciva = porciva;
      this.usucrea = usucrea;
      this.feccrea = feccrea;
      this.idfactura_facturas = idfactura_facturas;
      this.idcaja_cajas = idcaja_cajas;
      this.nrofac = nrofac;
   }

   public Long getIdcaja_cajas() {
      return idcaja_cajas;
   }
   public void setIdcaja_cajas(Long idcaja_cajas) {
      this.idcaja_cajas = idcaja_cajas;
   }

   public String getNrofac() {
      return nrofac;
   }
   public void setNrofac(String nrofac) {
      this.nrofac = nrofac;
   }

   public Long getIdfacelectro() {
      return idfacelectro;
   }
   public void setIdfacelectro(Long idfacelectro) {
      this.idfacelectro = idfacelectro;
   }

   public String getCodigonumerico() {
      return codigonumerico;
   }
   public void setCodigonumerico(String codigonumerico) {
      this.codigonumerico = codigonumerico;
   }
   public String getDigitoverificador() {
      return digitoverificador;
   }
   public void setDigitoverificador(String digitoverificador) {
      this.digitoverificador = digitoverificador;
   }
   public String getNombrexml() {
      return nombrexml;
   }
   public void setNombrexml(String nombrexml) {
      this.nombrexml = nombrexml;
   }
   public Integer getEstado() {
      return estado;
   }
   public void setEstado(Integer estado) {
      this.estado = estado;
   }
   public String getClaveacceso() {
      return claveacceso;
   }
   public void setClaveacceso(String claveacceso) {
      this.claveacceso = claveacceso;
   }
   public Integer getSwenviada() {
      return swenviada;
   }
   public void setSwenviada(Integer swenviada) {
      this.swenviada = swenviada;
   }
   public String getIdentificacion() {
      return identificacion;
   }
   public void setIdentificacion(String identificacion) {
      this.identificacion = identificacion;
   }
   public String getNombre() {
      return nombre;
   }
   public void setNombre(String nombre) {
      this.nombre = nombre;
   }
   public String getDireccion() {
      return direccion;
   }
   public void setDireccion(String direccion) {
      this.direccion = direccion;
   }
   public String getTelefono() {
      return telefono;
   }
   public void setTelefono(String telefono) {
      this.telefono = telefono;
   }
   public String getConcepto() {
      return concepto;
   }
   public void setConcepto(String concepto) {
      this.concepto = concepto;
   }
   public Float getBase0() {
      return base0;
   }
   public void setBase0(Float base0) {
      this.base0 = base0;
   }
   public Float getBaseimponiva() {
      return baseimponiva;
   }
   public void setBaseimponiva(Float baseimponiva) {
      this.baseimponiva = baseimponiva;
   }
   public Float getIva12() {
      return iva12;
   }
   public void setIva12(Float iva12) {
      this.iva12 = iva12;
   }
   public Float getIva0() {
      return iva0;
   }
   public void setIva0(Float iva0) {
      this.iva0 = iva0;
   }
   public Float getTotal() {
      return total;
   }
   public void setTotal(Float total) {
      this.total = total;
   }
   public String getNumautorizacion() {
      return numautorizacion;
   }
   public void setNumautorizacion(String numautorizacion) {
      this.numautorizacion = numautorizacion;
   }
   public String getMensaje() {
      return mensaje;
   }
   public void setMensaje(String mensaje) {
      this.mensaje = mensaje;
   }
   public String getTpidentifica() {
      return tpidentifica;
   }
   public void setTpidentifica(String tpidentifica) {
      this.tpidentifica = tpidentifica;
   }
   public Float getImpuesto() {
      return impuesto;
   }
   public void setImpuesto(Float impuesto) {
      this.impuesto = impuesto;
   }
   public Float getImpuestoretener() {
      return impuestoretener;
   }
   public void setImpuestoretener(Float impuestoretener) {
      this.impuestoretener = impuestoretener;
   }
   public Float getPorciva() {
      return porciva;
   }
   public void setPorciva(Float porciva) {
      this.porciva = porciva;
   }
   public Long getUsucrea() {
      return usucrea;
   }
   public void setUsucrea(Long usucrea) {
      this.usucrea = usucrea;
   }
   public Date getFeccrea() {
      return feccrea;
   }
   public void setFeccrea(Date feccrea) {
      this.feccrea = feccrea;
   }
   public Facturas getIdfactura_facturas() {
      return idfactura_facturas;
   }
   public void setIdfactura_facturas(Facturas idfactura_facturas) {
      this.idfactura_facturas = idfactura_facturas;
   }

   // public Cajas getIdcaja_cajas() {
   //    return idcaja_cajas;
   // }
   // public void setIdcaja_cajas(Cajas idcaja_cajas) {
   //    this.idcaja_cajas = idcaja_cajas;
   // }
   
}
