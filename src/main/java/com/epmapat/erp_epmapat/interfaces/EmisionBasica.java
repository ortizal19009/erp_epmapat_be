package com.epmapat.erp_epmapat.interfaces;

import java.util.Date;

public interface EmisionBasica {
    Long getIdemision();
    String getEmision();
    Integer getEstado();
    String getObservaciones();
    Date getFeccrea();
    Date getFechacierre();
    java.math.BigDecimal getM3();
}
