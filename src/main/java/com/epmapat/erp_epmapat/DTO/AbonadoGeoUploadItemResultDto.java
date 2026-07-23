package com.epmapat.erp_epmapat.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbonadoGeoUploadItemResultDto {
    private Long idabonado;
    private boolean success;
    private String mensaje;
    private String geolocalizacion;
}
