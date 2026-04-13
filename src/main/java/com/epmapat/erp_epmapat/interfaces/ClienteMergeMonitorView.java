package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDateTime;

public interface ClienteMergeMonitorView {
    Long getIdMerge();

    Long getMasterId();

    String getMasterNombre();

    String getMasterCedula();

    LocalDateTime getFechaMerge();

    Long getUsuarioMerge();

    String getObservacion();

    Long getClientesCount();

    Long getAbonadosCount();

    Long getFacturasCount();

    Long getLecturasCount();
}
