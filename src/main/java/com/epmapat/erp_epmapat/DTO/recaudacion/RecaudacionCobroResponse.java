package com.epmapat.erp_epmapat.DTO.recaudacion;

import java.math.BigDecimal;
import java.util.List;

import com.epmapat.erp_epmapat.DTO.ValorFactDTO;
import com.epmapat.erp_epmapat.modelo.Recaudacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionCobroResponse {
    private Recaudacion recaudacion;
    private RecaudacionCajaDTO caja;
    private List<ValorFactDTO> facturas;
    private BigDecimal totalCalculado;
    private String numeroFacturaSiguiente;
}
