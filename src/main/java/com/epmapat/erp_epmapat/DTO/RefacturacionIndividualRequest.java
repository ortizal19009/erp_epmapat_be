package com.epmapat.erp_epmapat.DTO;

import lombok.Data;

@Data
public class RefacturacionIndividualRequest {
    private Long idemision;
    private Long idabonado;
    private Long idrutaxemision;
    private Long idlecturaanterior;
    private Float lecturaanterior;
    private Float lecturaactual;
    private Long idnovedad;
    private Long idusuario;
    private Boolean swmulta;
}
