package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface CarteraVencidaFacturas {
    Long getFactura();

    String getNombre();

    String getModulo();

    BigDecimal getTotal();

    Long getCuenta();

    Long getEmision();

    Long getM3();

    Long getIdcliente();
}
