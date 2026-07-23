package com.epmapat.erp_epmapat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbonadoGeoPreviewDto {
    private Long idabonado;
    private String nombre;
    private String geolocalizacion;
}
