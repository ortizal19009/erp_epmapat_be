package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RefacturacionIndividualResponse {
    private Long idfactura;
    private Long idlectura;
    private Long idemisionindividual;
    private BigDecimal total;
}
