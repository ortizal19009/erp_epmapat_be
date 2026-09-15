package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacturasSinCobroInter {
    Long getIdfactura();

    Float getSubtotal();

    String getNombre();

    String getCedula();

    String getDireccionubicacion();

    Long getCuenta();

    LocalDate getFectransferencia();

    Long getFormapago();

    Long getIdmodulo();

    String getModulo();

    LocalDate getFeccrea();

    BigDecimal getIntereses();

    BigDecimal getTotal();

    Long getNum_facturas();
}
