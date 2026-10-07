package com.epmapat.erp_epmapat.rrhh.dto;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

public final class ThLeaveReconciliationCsv {
    private ThLeaveReconciliationCsv() {}

    public static byte[] render(ThLeaveReconciliationResponse report) {
        StringBuilder csv = new StringBuilder("\uFEFF");
        csv.append("Personal;Verificado;Año;Saldo;Activo;Estado libro;Asignados actuales;Usados actuales;Disponibles actuales;Apertura;Consumos;Reintegros;Ajustes;Disponibles libro;Usados libro;Asignados libro;Diferencia disponibles;Diferencia usados;Diferencia asignados;Movimientos;Solicitudes sin consumo;Observaciones\r\n");
        for (ThLeaveReconciliationResponse.Ejercicio r : report.getEjercicios()) {
            csv.append(Arrays.stream(new Object[] { report.getIdpersonal(), report.getGenerado_en(), r.getAnio(),
                    r.getIdbalance(), r.getActivo(), r.getEstado_libro(), r.getAsignados_actuales(), r.getUsados_actuales(),
                    r.getDisponibles_actuales(), r.getApertura(), r.getConsumos(), r.getReintegros(), r.getAjustes(),
                    r.getDisponibles_libro(), r.getUsados_libro(), r.getAsignados_libro(), r.getDiferencia_disponibles(),
                    r.getDiferencia_usados(), r.getDiferencia_asignados(), r.getMovimientos(),
                    r.getSolicitudes_sin_consumo().stream().map(String::valueOf).collect(Collectors.joining(", ")),
                    String.join(" | ", r.getAlertas()) })
                    .map(ThLeaveReconciliationCsv::cell).collect(Collectors.joining(";"))).append("\r\n");
        }
        return csv.toString().getBytes(StandardCharsets.UTF_8);
    }

    static String cell(Object value) {
        if (value == null) return "";
        if (value instanceof BigDecimal) return ((BigDecimal) value).toPlainString();
        if (value instanceof Number) return value.toString();
        String text = value.toString();
        // Text cells never execute as formulas when the report is opened in a spreadsheet.
        if (text.stripLeading().matches("(?s)^[=+@-].*")) text = "'" + text;
        return "\"" + text.replace("\"", "\"\"") + "\"";
    }
}
