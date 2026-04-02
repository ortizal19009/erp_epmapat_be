package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface ConvenioDetalle {
    Long getIdconvenio();

    Long getNroconvenio();

    String getNombre();

    Long getIdabonado();

    LocalDate getFeccrea();

    Long getEstado();

    String getNroautorizacion();

    String getReferencia();

    BigDecimal getTotalconvenio();

    Long getCuotas();

    BigDecimal getCuotainicial();

    BigDecimal getPagomensual();

    BigDecimal getCuotafinal();

    Long getFacantiguas();

    Long getFacnuevas();

    Long getFacpagadas();

    Long getFacpendientes();
}
