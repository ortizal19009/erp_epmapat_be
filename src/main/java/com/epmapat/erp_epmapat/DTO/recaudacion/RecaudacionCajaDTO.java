package com.epmapat.erp_epmapat.DTO.recaudacion;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecaudacionCajaDTO {
    private Long idcaja;
    private Long idrecaudaxcaja;
    private Integer estado;
    private String username;
    private String establecimiento;
    private String codigo;
    private Long facinicio;
    private Long facfin;
    private Long secuencial;
    private Long siguienteSecuencial;
}
