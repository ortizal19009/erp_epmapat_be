package com.epmapat.erp_epmapat.DTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AnularEmisionRequest {
    private Long iddocumento;
    private String referenciaDocumento;
    private String motivo;
}
