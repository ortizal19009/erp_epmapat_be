package com.epmapat.erp_epmapat.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmisionGeneracionResponseDTO {
    private Long idemision;
    private String emision;
    private long totalRutasEsperadas;
    private long rutasExistentes;
    private long rutasCreadas;
    private long rutasCompletas;
    private long rutasPendientes;
    private long totalLecturasEsperadas;
    private long lecturasExistentes;
    private long lecturasCreadas;
    private long lecturasPendientes;
    @Builder.Default
    private List<EmisionGeneracionRutaDetalleDTO> rutas = new ArrayList<>();
}
