package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.util.Date;

public interface EmisionControlResumen {
    Long getIdemision();
    String getEmision();
    Integer getEstado();
    Date getFeccrea();
    Date getFechacierre();
    BigDecimal getM3();
    Long getTotalLecturas();
    Long getLecturasCargadas();
}
