package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface CierreRutaRubroResumen {
    Long getIdrubro();
    String getDescripcion();
    BigDecimal getTotal();
    Long getAbonados();
}
