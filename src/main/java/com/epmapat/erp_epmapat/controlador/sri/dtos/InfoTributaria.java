package com.epmapat.erp_epmapat.controlador.sri.dtos;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class InfoTributaria {
   private Integer ambiente;
   private Integer tipoEmision;
   private String razonSocial;
   private String ruc;
   private String claveAcceso;
   private String codDoc;
   private String estab;
   private String ptoEmi;
   private String secuencial;
   private String dirMatriz;
}
