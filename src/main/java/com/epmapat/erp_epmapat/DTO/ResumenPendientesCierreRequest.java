package com.epmapat.erp_epmapat.DTO;

import java.util.List;

public record ResumenPendientesCierreRequest(
        List<Long> cuentas,
        List<Long> facturasExcluidas) {
}
