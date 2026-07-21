package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface ControlRutaStats {
    Long getIdrutaxemision();
    Long getIdruta();
    String getCodigoRuta();
    String getNombreRuta();
    Integer getEstadoRuta();
    Long getLecturas();
    Long getLecturasTomadas();
    Long getLecturasConFactura();
    Long getLecturasSinFactura();
    Long getAbonados();
    BigDecimal getM3();
    BigDecimal getEmitido();
    BigDecimal getCobrado();
    BigDecimal getPendiente();
}
