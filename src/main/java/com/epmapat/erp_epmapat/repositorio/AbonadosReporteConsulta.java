package com.epmapat.erp_epmapat.repositorio;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class AbonadosReporteConsulta {
    private final JdbcTemplate jdbc;

    public AbonadosReporteConsulta(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public List<Map<String, Object>> medidoresAntiguos(LocalDate fechaCorte) {
        return jdbc.queryForList("""
            SELECT a.idabonado, a.nromedidor, c.nombre AS cliente,
                   a.fechainstalacion, a.lecturainicial,
                   l.fechalectura, l.lecturaactual,
                   CAST(l.lecturaactual AS numeric) - a.lecturainicial AS acumuladom3
            FROM abonados a
            LEFT JOIN clientes c ON c.idcliente = a.idcliente_clientes
            JOIN LATERAL (
                SELECT h.fechalectura, h.lecturaactual
                FROM lecturas h
                WHERE h.idabonado_abonados = a.idabonado
                  AND h.fechalectura >= a.fechainstalacion
                  AND h.fechalectura < ?
                  AND h.lecturaactual IS NOT NULL
                ORDER BY h.fechalectura DESC, h.idemision DESC NULLS LAST, h.idlectura DESC
                LIMIT 1
            ) l ON TRUE
            WHERE a.fechainstalacion < ?
              AND CAST(l.lecturaactual AS numeric) - a.lecturainicial > 5000
            ORDER BY a.idabonado
            """, Date.valueOf(fechaCorte.plusDays(1)), Date.valueOf(fechaCorte.minusYears(7)));
    }
}
