package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;

/** Read-only verification of recorded balances, not certification of historical entitlements. */
@Value
public class ThLeaveReconciliationResponse {
    Long idpersonal;
    LocalDateTime generado_en;
    List<Ejercicio> ejercicios;

    @Value
    public static class Ejercicio {
        Integer anio;
        Long idbalance;
        Boolean activo;
        String estado_libro;
        BigDecimal asignados_actuales;
        BigDecimal usados_actuales;
        BigDecimal disponibles_actuales;
        BigDecimal apertura;
        BigDecimal consumos;
        BigDecimal reintegros;
        BigDecimal ajustes;
        BigDecimal disponibles_libro;
        BigDecimal usados_libro;
        BigDecimal asignados_libro;
        BigDecimal diferencia_disponibles;
        BigDecimal diferencia_usados;
        BigDecimal diferencia_asignados;
        int movimientos;
        List<Long> solicitudes_sin_consumo;
        List<String> alertas;
    }
}
