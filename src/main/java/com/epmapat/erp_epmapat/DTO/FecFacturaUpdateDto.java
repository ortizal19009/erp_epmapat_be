package com.epmapat.erp_epmapat.DTO;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Data
public class FecFacturaUpdateDto {
    private String estado;
    private String claveacceso;
    private String xmlautorizado;
    private String errores; // opcional
    // getters/setters
}