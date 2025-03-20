package com.epmapat.erp_epmapat.DTO;

import java.math.BigDecimal;

import lombok.*;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ValorFactDTO {
    private Long idfactura;
    private BigDecimal subtotal;
    private BigDecimal total;
    private BigDecimal interes;
    private Integer numfacturas;
    private Long cuenta;
    private String nombre;
    private String cedula;
    private String direccionubicacion;
}
