package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface CVClientes {
    long getIdcliente();
    String getPlanilla();
    BigDecimal getValor();
    String getNombre();
    String getCedula();
    String getDireccion();
    String getEmail();
    String getModulo();
    String getTelefono();
}
