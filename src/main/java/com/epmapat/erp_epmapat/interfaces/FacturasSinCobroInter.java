package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface FacturasSinCobroInter {
    Long getIdfactura();
    BigDecimal getSubtotal();
    String getNombre();
    String getCedula();
    String getDireccionubicacion();
    Long getCuenta();
}
