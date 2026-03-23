package com.epmapat.erp_epmapat.DTO.contabilidad;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class SaldoCuentasDTO {

   private String codcue;
   private String nomcue;

   private BigDecimal saldoAnterior; 
   private BigDecimal debitosPeriodo; 
   private BigDecimal creditosPeriodo; 
   private BigDecimal saldoPeriodo; 
   private BigDecimal saldoFinal;

}
