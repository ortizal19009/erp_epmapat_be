package com.epmapat.erp_epmapat.interfaces;

import java.time.LocalDate;
import java.math.BigDecimal;


public interface FacTransferencias {
    Long getIdfactura();
    BigDecimal getTotal();
    LocalDate getFechatransferencia();
    Long getIdmodulo();
    String getNombre();
}
