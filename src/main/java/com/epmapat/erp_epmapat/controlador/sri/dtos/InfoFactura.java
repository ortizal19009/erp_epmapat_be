package com.epmapat.erp_epmapat.controlador.sri.dtos;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;
@Data
@Getter
@Setter
public class InfoFactura {
    private LocalDateTime fechaEmision;
    private String dirEstablecimiento;
    private String contribuyenteEspecial;
    private String obligadoContabilidad;
    private String tipoIdentificacionComprador;
    private String razonSocialComprador;
    private String identificacionComprador;
    private BigDecimal totalSinImpuestos;
    private BigDecimal totalDescuento;
    public void setFechaEmision(String format) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFechaEmision'");
    }

}
