package com.epmapat.erp_epmapat.interfaces;

import java.math.BigDecimal;

public interface ResumenPendientesCuenta {
    Long getCuenta();
    Long getFacturasConsumo();
    BigDecimal getCapitalConsumo();
    BigDecimal getInteresConsumo();
    BigDecimal getValorConsumo();
    Long getFacturasServicios();
    BigDecimal getCapitalServicios();
    BigDecimal getInteresServicios();
    BigDecimal getValorServicios();
    Long getFacturasConvenios();
    BigDecimal getCapitalConvenios();
    BigDecimal getInteresConvenios();
    BigDecimal getValorConvenios();
    Long getTotalFacturasPendientes();
    BigDecimal getTotalIntereses();
    BigDecimal getTotalPendiente();
}
