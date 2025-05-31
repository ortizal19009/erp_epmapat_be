package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface ResEmisiones {
    Long getIdemision();

    String getEmision();

    BigDecimal getValemision();

    Long getM3();

    Long getNcuentas();

    BigDecimal getTotal_pagado();

    BigDecimal getTotal_pendiente();
}
