package com.epmapat.erp_epmapat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FecFacturaGestionFiltroDto {
   private String numeroFactura;
   private String claveAcceso;
   private String estadoSri;
   private String cliente;
   private String identificacion;
   private String establecimiento;
   private String puntoEmision;
   private Long idusuario;
   private String secuencialDesde;
   private String secuencialHasta;
   private String fechaDesde;
   private String fechaHasta;
   private String emailEstado;
   private Boolean swmail;
   private Integer mailIntentos;
   private String mailError;
   private Boolean soloFallidos;
   private Integer limit;
}
