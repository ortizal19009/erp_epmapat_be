package com.epmapat.erp_epmapat.rrhh.dto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/** Exports one bounded page, using the same minimal projection as the inbox. */
public final class ThLeaveInboxCsv {
    private ThLeaveInboxCsv() {}

    public static byte[] render(ThLeaveInboxResponse page) {
        StringBuilder csv = new StringBuilder("\uFEFFPágina;Tamaño página;Total solicitudes;Solicitud;Personal;Nombres;Apellidos;Tipo;Estado;Desde;Hasta;Días;Motivo;Creada;Aprobador;Fecha aprobación;Observación aprobación;Resuelto por;Fecha resolución;Motivo resolución\r\n");
        for (ThLeaveInboxResponse.Solicitud r : page.getContenido()) {
            csv.append(Arrays.stream(new Object[] { page.getPagina() + 1, page.getTamano(), page.getTotal_elementos(),
                    r.getIdrequest(), r.getIdpersonal(), r.getNombres(), r.getApellidos(), r.getTipolicencia(),
                    r.getEstado(), r.getFechainicio(), r.getFechafin(), r.getDias_solicitados(), r.getMotivo(),
                    r.getFeccrea(), r.getAprobador_id(), r.getFecha_aprobacion(), r.getObservacion_aprobacion(),
                    r.getResuelto_por(), r.getFecha_resolucion(), r.getMotivo_resolucion() })
                    .map(ThLeaveReconciliationCsv::cell).collect(Collectors.joining(";"))).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
