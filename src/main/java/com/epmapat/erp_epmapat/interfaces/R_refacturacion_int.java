package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.util.Date;

public interface R_refacturacion_int {
    Long getCuenta();
    String getNombre();
    Long getNuevaplanilla();
    BigDecimal getValornuevo();
    Long getAnteriorplanilla();
    BigDecimal  getValoranterior();
    String getObservaciones();
    Date getFecelimina();
}
