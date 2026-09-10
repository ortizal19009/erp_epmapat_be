package com.epmapat.erp_epmapat.interfaces;

import java.util.Date;

public interface EmisionIndividualListado {
    Long getIdemisionindividual();
    Long getIdemision();
    Long getEmision();
    Long getCuenta();
    String getNombre();
    String getCedula();
    Date getFechaemision();
    Long getIdlecturaanterior();
    Long getIdlecturanueva();
    Long getIdfacturaanterior();
    Long getIdfacturanueva();
    Float getLecturaanterioranterior();
    Float getLecturaactualanterior();
    Float getLecturaanteriornueva();
    Float getLecturaactualnueva();
}
