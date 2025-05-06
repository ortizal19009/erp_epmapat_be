package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface FacEliminadas {
Long getIdfactura(); 
Long getCuenta();
BigDecimal getTotal();
String getNombre();
LocalDate getFechaeliminacion();
String getRazoneliminacion();
String getUsuario();

}
