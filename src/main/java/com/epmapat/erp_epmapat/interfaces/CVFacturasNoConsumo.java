package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CVFacturasNoConsumo {
    Long getFactura();

    String getNombre();

    String getModulo();

    BigDecimal getTotal();

    Long getCuenta();

    BigDecimal getTotalFactura();
    LocalDate getFeccrea();
    Long getIdcliente();

}
