package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import lombok.Value;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveMovement;

@Value
public class ThLeaveMovementResponse {
    Long idmovement;
    Long idbalance;
    Integer anio;
    Long idrequest;
    Long idmovement_origen;
    String tipo;
    BigDecimal dias;
    BigDecimal disponibles_antes;
    BigDecimal disponibles_despues;
    BigDecimal usados_antes;
    BigDecimal usados_despues;
    BigDecimal asignados;
    Long usuario;
    LocalDateTime fecha;
    String motivo;

    public static ThLeaveMovementResponse from(ThLeaveMovement m) {
        return new ThLeaveMovementResponse(m.getIdmovement(), m.getBalance().getIdbalance(), m.getBalance().getAnio(),
                m.getRequest() == null ? null : m.getRequest().getIdrequest(),
                m.getOrigen() == null ? null : m.getOrigen().getIdmovement(), m.getTipo().name(), m.getDias(),
                m.getDisponibles_antes(), m.getDisponibles_despues(), m.getUsados_antes(), m.getUsados_despues(),
                m.getAsignados(), m.getUsuario(), m.getFecha(), m.getMotivo());
    }
}
