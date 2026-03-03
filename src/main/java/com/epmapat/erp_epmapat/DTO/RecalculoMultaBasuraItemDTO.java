package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;

public record RecalculoMultaBasuraItemDTO(

        Long cuenta,
        Long idfactura,
        String estado,
        BigDecimal valorAplicado,
        BigDecimal totalNuevo

) {
}