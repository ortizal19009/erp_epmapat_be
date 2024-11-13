package com.epmapat.erp_epmapat.interfaces;
import java.math.BigDecimal;

public interface CVFacturasNoConsumo {
    Long getFactura(); 
    String getNombre();
    String getModulo();
    BigDecimal getTotal();
    Long getCuenta();
}
