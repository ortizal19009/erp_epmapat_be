package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface RecaudaFacturasI {
Long getIdfactura();
String getNombre();
String getNrofactura();
Long getEstado();
Long getFormapago();
BigDecimal getValor();
LocalDate getFechacobro();
String getNomusu();
Long getIdabonado();
}
