package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@Getter
@Setter
@NoArgsConstructor
public class RemiDTO {
    private Long idfactura;
    private String descripcion;
    private LocalDate feccrea;
    private BigDecimal total;
    private BigDecimal intereses;
    private String nrofactura;

}
