package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface NtaCreditoCompPago {
    LocalDate getFechaaplicado();

    BigDecimal getValor();

    BigDecimal getSaldo();

    Long getIdfactura();

    Long getIdntacredito_ntacredito();
}
