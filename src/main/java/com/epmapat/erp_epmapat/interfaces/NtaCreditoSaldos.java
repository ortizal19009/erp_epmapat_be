package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface NtaCreditoSaldos {
    Long getIdntacredito();

    Long getCuenta();

    BigDecimal getSaldo();

    BigDecimal getDevengado();
}
