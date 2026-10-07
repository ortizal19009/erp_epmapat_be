package com.epmapat.erp_epmapat.rrhh.dto;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Value;

@Value
public class ThLeaveHistoryResponse {
    ThLeaveInboxResponse.Solicitud solicitud;
    LocalDateTime consultado_en;
    List<Evento> eventos;
    List<ThLeaveMovementResponse> movimientos;
    List<String> advertencias;

    @Value
    public static class Evento {
        Long idaudit;
        String accion;
        String detalle;
        Long usuario;
        LocalDateTime fecha;
    }
}
