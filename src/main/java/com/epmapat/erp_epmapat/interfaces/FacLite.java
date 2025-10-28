package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;
public interface FacLite {
    Long getId();
    BigDecimal getSuma();
    Integer getFormaPago();
    // Si tu columna es DATE -> LocalDate; si es TIMESTAMP -> LocalDateTime
    java.time.LocalDate getFecCrea();      // o LocalDateTime si aplica
    java.time.LocalDate getFecTransfer();  // o LocalDateTime si aplica
}

