package com.epmapat.erp_epmapat.modelo.contabilidad;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fec_retenciones_impuestos")
public class Fec_reteimpu implements Serializable {
   @Id
   private Long idretencionesimpuestos;
   private Long idretencion;
   private String codigo;
   private String codigoporcentaje;
   private BigDecimal baseimponible;
   private String codigodocumentosustento;
   private String numerodocumentosustento;
   private Date fechaemisiondocumentosustento;
      
}
