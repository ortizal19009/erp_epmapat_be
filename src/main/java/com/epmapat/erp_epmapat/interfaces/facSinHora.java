package com.epmapat.erp_epmapat.interfaces;

import java.security.Timestamp;
import java.sql.Time;


public interface facSinHora {
    Long getIdfactura();
    Time getHoracobro();
    Timestamp getFechacobro();
    Time getHora();
}
