package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface CierreRutaMultaDetalle {
    Long getCuenta();
    String getNombre();
    String getCategoria();
    Long getIdfactura();
    String getNrofactura();
    BigDecimal getMulta();
    Long getPendientesalcierre();
    String getFacturaspendientes();
    BigDecimal getTotalfactura();
    LocalDate getFechacobro();
}
