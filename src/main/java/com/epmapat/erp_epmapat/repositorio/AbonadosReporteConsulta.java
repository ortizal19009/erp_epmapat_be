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
            WITH ultimas AS (
                SELECT DISTINCT ON (h.idabonado_abonados)
                       h.idabonado_abonados, h.fechalectura, h.lecturaactual
                FROM lecturas h
                JOIN abonados b ON b.idabonado = h.idabonado_abonados
                WHERE h.fechalectura >= b.fechainstalacion
                  AND h.fechalectura < ?
                  AND b.fechainstalacion < ?
                  AND h.lecturaactual IS NOT NULL
                ORDER BY h.idabonado_abonados, h.fechalectura DESC,
                         h.idemision DESC NULLS LAST, h.idlectura DESC
            )
            SELECT a.idabonado, a.nromedidor, c.nombre AS cliente,
                   r.descripcion AS ruta, cat.descripcion AS categoria, a.estado,
                   a.fechainstalacion, a.lecturainicial,
                   l.fechalectura, l.lecturaactual,
                   CAST(l.lecturaactual AS numeric) - a.lecturainicial AS acumuladom3
            FROM abonados a
            LEFT JOIN clientes c ON c.idcliente = a.idcliente_clientes
            LEFT JOIN rutas r ON r.idruta = a.idruta_rutas
            LEFT JOIN categorias cat ON cat.idcategoria = a.idcategoria_categorias
            JOIN ultimas l ON l.idabonado_abonados = a.idabonado
            WHERE CAST(l.lecturaactual AS numeric) - a.lecturainicial > 5000
            ORDER BY a.idabonado
            """, Date.valueOf(fechaCorte.plusDays(1)), Date.valueOf(fechaCorte.minusYears(7)));
    }
}
