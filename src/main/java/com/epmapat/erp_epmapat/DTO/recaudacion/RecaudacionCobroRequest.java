package com.epmapat.erp_epmapat.DTO.recaudacion;

import java.util.List;

import com.epmapat.erp_epmapat.modelo.Recaudacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionCobroRequest {
    private List<Long> facturas;
    private Long autentification;
    private Recaudacion recaudacion;
    private Long idcaja;
}
