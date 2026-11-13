package com.epmapat.erp_epmapat.controlador;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.epmapat.erp_epmapat.repositorio.AbonadosReporteConsulta;

@RestController
@RequestMapping("/abonados/reportes")
public class AbonadosReportesApi {
    private final AbonadosReporteConsulta consulta;

    public AbonadosReportesApi(AbonadosReporteConsulta consulta) {
        this.consulta = consulta;
    }

    @GetMapping("/medidores-antiguos")
    public Map<String, Object> medidoresAntiguos(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaCorte) {
        List<Map<String, Object>> detalle = consulta.medidoresAntiguos(fechaCorte);
        return Map.of("fechaCorte", fechaCorte.toString(),
                "descripcion", "Cantidad total instalados mayores a 7 años con lecturas acumuladas mayores a 5.000 m³ desde su instalación",
                "cantidad", detalle.size(), "detalle", detalle);
    }
}
