package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDate;

public interface EstadoConvenios {
    Long getIdconvenio();

    Long getNroconvenio();

    Long getIdabonado();

    String getNombre();

    LocalDate getFeccrea();

    Long getEstado();

    Long getFacantiguas();

    Long getFacnuevas();

    Long getFacpagadas();

    Long getFacpendientes();
}
