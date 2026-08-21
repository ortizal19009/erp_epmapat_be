package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pliego24Dto {
    private Long idpliego;
    private Integer desde;
    private Integer hasta;
    private BigDecimal agua;
    private BigDecimal saneamiento;
    private Long idcategoria;
    private BigDecimal porc;
}
