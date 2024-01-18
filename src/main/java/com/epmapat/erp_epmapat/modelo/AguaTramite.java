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
@Table(name="aguatramite")

public class AguaTramite {
   
   @Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long idaguatramite;
   private String codmedidor;
   private String comentario;
   private Integer estado;
   private String sistema;
   private Date fechaterminacion;
   private String observacion;
   private Long idfactura_facturas; //No todos los trámites tienen factura
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idcliente_clientes")
	private Clientes idcliente_clientes;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtipotramite_tipotramite")
	private TipoTramite idtipotramite_tipotramite;
   private Long usucrea; 
   private Date feccrea;
	private Long usumodi;
   private Date fecmodi;

   public Long getIdaguatramite() {
      return idaguatramite;
   }
   public void setIdaguatramite(Long idaguatramite) {
      this.idaguatramite = idaguatramite;
   }

   public String getCodmedidor() {
      return codmedidor;
   }
   public void setCodmedidor(String codmedidor) {
      this.codmedidor = codmedidor;
      
   }
   public String getComentario() {
      return comentario;
   }
   public void setComentario(String comentario) {
      this.comentario = comentario;
   }
   public Integer getEstado() {
      return estado;
   }
   public void setEstado(Integer estado) {
      this.estado = estado;
   }
   public String getSistema() {
      return sistema;
   }
   public void setSistema(String sistema) {
      this.sistema = sistema;
   }
   public Date getFechaterminacion() {
      return fechaterminacion;
   }
   public void setFechaterminacion(Date fechaterminacion) {
      this.fechaterminacion = fechaterminacion;
   }
   public String getObservacion() {
      return observacion;
   }
   public void setObservacion(String observacion) {
      this.observacion = observacion;
   }
   public Long getIdfactura_facturas() {
      return idfactura_facturas;
   }
   public void setIdfactura_facturas(Long idfactura_facturas) {
      this.idfactura_facturas = idfactura_facturas;
   }
   public Clientes getIdcliente_clientes() {
      return idcliente_clientes;
   }
   public void setIdcliente_clientes(Clientes idcliente_clientes) {
      this.idcliente_clientes = idcliente_clientes;
   }
   public TipoTramite getIdtipotramite_tipotramite() {
      return idtipotramite_tipotramite;
   }
   public void setIdtipotramite_tipotramite(TipoTramite idtipotramite_tipotramite) {
      this.idtipotramite_tipotramite = idtipotramite_tipotramite;
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
   public Long getUsumodi() {
      return usumodi;
   }
   public void setUsumodi(Long usumodi) {
      this.usumodi = usumodi;
   }
   public Date getFecmodi() {
      return fecmodi;
   }
   public void setFecmodi(Date fecmodi) {
      this.fecmodi = fecmodi;
   }

   


}
