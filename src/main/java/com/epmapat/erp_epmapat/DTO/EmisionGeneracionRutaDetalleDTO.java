package com.epmapat.erp_epmapat.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmisionGeneracionRutaDetalleDTO {
    private Long idruta;
    private String codigoRuta;
    private String nombreRuta;
    private Long idrutaxemision;
    private boolean rutaCreada;
    private long abonadosEsperados;
    private long lecturasExistentes;
    private long lecturasCreadas;
    private long lecturasPendientes;
}
