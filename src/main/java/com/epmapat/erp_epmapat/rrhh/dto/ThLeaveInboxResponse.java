package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import com.epmapat.erp_epmapat.rrhh.modelo.Personal;
import com.epmapat.erp_epmapat.rrhh.modelo.ThLeaveRequest;
import lombok.Value;

@Value
public class ThLeaveInboxResponse {
    List<Solicitud> contenido;
    int pagina;
    int tamano;
    long total_elementos;
    int total_paginas;

    @Value
    public static class Solicitud {
        Long idrequest;
        Long idpersonal;
        String nombres;
        String apellidos;
        String tipolicencia;
        LocalDate fechainicio;
        LocalDate fechafin;
        BigDecimal dias_solicitados;
        String motivo;
        String estado;
        LocalDate feccrea;
        Long aprobador_id;
        LocalDate fecha_aprobacion;
        String observacion_aprobacion;
        Long resuelto_por;
        LocalDateTime fecha_resolucion;
        String motivo_resolucion;

        public static Solicitud from(ThLeaveRequest r) {
            Personal p = r.getIdpersonal_personal();
            return new Solicitud(r.getIdrequest(), p == null ? null : p.getIdpersonal(),
                    p == null ? null : p.getNombres(), p == null ? null : p.getApellidos(),
                    r.getTipolicencia() == null ? null : r.getTipolicencia().trim().toUpperCase(java.util.Locale.ROOT),
                    r.getFechainicio(), r.getFechafin(), r.getDias_solicitados(),
                    r.getMotivo(), r.getEstado() == null ? null : r.getEstado().trim().toUpperCase(java.util.Locale.ROOT),
                    r.getFeccrea(), r.getAprobador_id(), r.getFecha_aprobacion(), r.getObservacion_aprobacion(),
                    r.getResuelto_por(), r.getFecha_resolucion(), r.getMotivo_resolucion());
        }
    }
}
