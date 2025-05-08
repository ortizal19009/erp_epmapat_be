package com.epmapat.erp_epmapat.controlador.sri.dtos;

import java.math.BigDecimal;
import com.epmapat.erp_epmapat.controlador.sri.models.Factura;

import lombok.*;
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Pago {
    private Long idfacturapagos;
    private Factura factura;
    private String formapago;
    private BigDecimal total;
    private Integer plazo;
    private String unidadtiempo;

}
