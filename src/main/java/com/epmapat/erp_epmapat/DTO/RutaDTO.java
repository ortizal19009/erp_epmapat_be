package com.epmapat.erp_epmapat.DTO;

import java.time.LocalDate;

import lombok.Data;

@Data
public class RutaDTO {
    private Long idruta;
    private String descripcion;
    private Long orden;
    private String codigo;
    private Long usucrea;
    private LocalDate feccrea;
    private Long usumodi;
    private LocalDate fecmodi;
    private Boolean estado;
}
