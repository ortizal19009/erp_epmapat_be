package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.util.Date;

public interface ControlRutaStats {
    Long getIdrutaxemision();
    Long getIdruta();
    String getCodigoRuta();
    String getNombreRuta();
    Date getFechacierre();
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
