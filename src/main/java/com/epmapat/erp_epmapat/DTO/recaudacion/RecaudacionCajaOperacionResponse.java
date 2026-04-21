package com.epmapat.erp_epmapat.DTO.recaudacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionCajaOperacionResponse {
    private String mensaje;
    private RecaudacionCajaDTO caja;
}
