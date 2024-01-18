package com.epmapat.erp_epmapat.modelo;
import java.util.Date;

import javax.persistence.*;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

@Entity
@Table(name = "liquidafac")

public class Liquidafac {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idliquidafac;
	private Long cuota;
	private Float valor;
	private Long usuarioeliminacion;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "fechaeliminacion")
	private Date fechaeliminacion;
	private String razoneliminacion;
	private Long estado;
	private String observacion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idfacturacion_facturacion")
	private Facturacion idfacturacion_facturacion;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name ="idfactura_facturas")
	private Facturas idfactura_facturas;
	private Long usucrea;
	@Temporal(TemporalType.DATE)
	@DateTimeFormat(iso = ISO.DATE)
	@Column(name = "feccrea")
	private Date feccrea;
   
   public Long getIdliquidafac() {
      return idliquidafac;
   }
   public void setIdliquidafac(Long idliquidafac) {
      this.idliquidafac = idliquidafac;
   }
   public Long getCuota() {
      return cuota;
   }
   public void setCuota(Long cuota) {
      this.cuota = cuota;
   }
   public Float getValor() {
      return valor;
   }
   public void setValor(Float valor) {
      this.valor = valor;
   }
   public Long getUsuarioeliminacion() {
      return usuarioeliminacion;
   }
   public void setUsuarioeliminacion(Long usuarioeliminacion) {
      this.usuarioeliminacion = usuarioeliminacion;
   }
   public Date getFechaeliminacion() {
      return fechaeliminacion;
   }
   public void setFechaeliminacion(Date fechaeliminacion) {
      this.fechaeliminacion = fechaeliminacion;
   }
   public String getRazoneliminacion() {
      return razoneliminacion;
   }
   public void setRazoneliminacion(String razoneliminacion) {
      this.razoneliminacion = razoneliminacion;
   }
   public Long getEstado() {
      return estado;
   }
   public void setEstado(Long estado) {
      this.estado = estado;
   }
   public String getObservacion() {
      return observacion;
   }
   public void setObservacion(String observacion) {
      this.observacion = observacion;
   }
   public Facturacion getIdfacturacion_facturacion() {
      return idfacturacion_facturacion;
   }
   public void setIdfacturacion_facturacion(Facturacion idfacturacion_facturacion) {
      this.idfacturacion_facturacion = idfacturacion_facturacion;
   }
   public Facturas getIdfactura_facturas() {
      return idfactura_facturas;
   }
   public void setIdfactura_facturas(Facturas idfactura_facturas) {
      this.idfactura_facturas = idfactura_facturas;
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

}
