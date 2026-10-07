package com.epmapat.erp_epmapat.rrhh.dto;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class ThLeaveMovementCsv {
    private ThLeaveMovementCsv() {}

    public static byte[] render(Long personal, List<ThLeaveMovementResponse> movements) {
        StringBuilder csv = new StringBuilder("\uFEFFPersonal;Movimiento;Saldo;Año;Solicitud;Movimiento origen;Tipo;Días;Disponible antes;Disponible después;Usados antes;Usados después;Asignados;Usuario;Fecha;Motivo\r\n");
        for (ThLeaveMovementResponse m : movements) {
            csv.append(Arrays.stream(new Object[] { personal, m.getIdmovement(), m.getIdbalance(), m.getAnio(),
                    m.getIdrequest(), m.getIdmovement_origen(), m.getTipo(), m.getDias(), m.getDisponibles_antes(),
                    m.getDisponibles_despues(), m.getUsados_antes(), m.getUsados_despues(), m.getAsignados(),
                    m.getUsuario(), m.getFecha(), m.getMotivo() })
                    .map(ThLeaveReconciliationCsv::cell).collect(Collectors.joining(";"))).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }
}
