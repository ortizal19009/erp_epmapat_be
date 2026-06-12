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
@Table(name="aguatramite")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private Clientes idcliente_clientes;
   @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "idtipotramite_tipotramite")
	@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	private TipoTramite idtipotramite_tipotramite;
   private Long usucrea; 
   private Date feccrea;
	private Long usumodi;
   private Date fecmodi;
   private Long iddocumento_documentos;
   private String nrodocumento;


}
