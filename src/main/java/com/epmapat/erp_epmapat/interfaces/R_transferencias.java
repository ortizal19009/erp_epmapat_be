package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface R_transferencias {
    Long getIdfactura();
    LocalDate getFechatransferencia();
    String getNrofactura();
    Long getFormapago(); 
    BigDecimal getValor();
    String getNombre();
    String getCedula();
    BigDecimal getSwiva();
}
