package com.epmapat.erp_epmapat.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SmartSyncSummaryDto {
    private int clientes;
    private int abonados;
    private int rutas;
    private int lecturas;
}
