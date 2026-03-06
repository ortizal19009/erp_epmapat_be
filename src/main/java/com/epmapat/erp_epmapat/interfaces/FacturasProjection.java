package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDate;

public interface FacturasProjection {
    Long getIdfactura();

    String getNrofactura();

    String getNombre();

    String getNomusu();

    LocalDate getFechacobro();

    String getDescripcion();

    String getEmision();

    Long getIdabonado_abonados();

}
