package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface RecaudaFacturasI {
Long getIdfactura();
String getNombre();
String getNrofactura();
Long getEstado();
Long getFormapago();
BigDecimal getValor();
}
