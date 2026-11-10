package com.epmapat.erp_epmapat.DTO;

import com.epmapat.erp_epmapat.modelo.AguaTramite;
import com.epmapat.erp_epmapat.modelo.Abonados;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AguaTramiteAplicacionRequest {
    private AguaTramite tramite;
    private Abonados abonado;
}
