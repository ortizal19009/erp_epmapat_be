package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface CVAbonados {
    Long getCuenta();

    Long getIdcliente();

    String getResponsable();

    String getIdentificacion();

    String getCategoria();

    String getRuta();

    Long getEstado();

    BigDecimal getValor();

    BigDecimal getInteres();

    BigDecimal getTotaldeuda();

    Long getFacturas();
}
