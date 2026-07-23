package com.epmapat.erp_epmapat.DTO;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AbonadoGeoUploadResultDto {
    private int actualizados;
    private List<String> errores;
    private List<AbonadoGeoUploadItemResultDto> detalles;
}
