package com.epmapat.erp_epmapat.DTO.recaudacion;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransferenciaFacturasRequest {
    private List<Long> facturas;
    private Long idusuario;
}
