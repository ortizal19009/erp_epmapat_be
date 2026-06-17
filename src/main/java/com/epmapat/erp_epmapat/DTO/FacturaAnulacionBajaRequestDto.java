package com.epmapat.erp_epmapat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FacturaAnulacionBajaRequestDto {
    private Long idfactura;
    private String accion;
    private String motivo;
    private Long idusuario;
}
