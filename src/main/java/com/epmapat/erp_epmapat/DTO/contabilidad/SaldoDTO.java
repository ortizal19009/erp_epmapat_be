package com.epmapat.erp_epmapat.DTO.contabilidad;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SaldoDTO {

   private BigDecimal debitos;
   private BigDecimal creditos;
   private BigDecimal saldo;

}
