package com.epmapat.erp_epmapat.controlador.sri.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoGeneracion {
    private Long idFactura;
    private String estado; // "OK" o "ERROR"
    private String mensaje;
    private byte[] xml;
}
