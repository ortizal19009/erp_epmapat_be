package com.epmapat.erp_epmapat.repositorio;

import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ClientesReporteConsulta {
    private final JdbcTemplate jdbc;
    public ClientesReporteConsulta(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public List<Map<String, Object>> listado() {
        return jdbc.queryForList("""
            WITH vinculos AS (
              SELECT idcliente_clientes cliente, idabonado cuenta FROM abonados WHERE idcliente_clientes IS NOT NULL
              UNION SELECT idresponsable, idabonado FROM abonados WHERE idresponsable IS NOT NULL
            ), cantidades AS (SELECT cliente, COUNT(*) cuentas FROM vinculos GROUP BY cliente)
            SELECT c.idcliente, c.nombre, c.cedula, n.descripcion nacionalidad, TO_CHAR(c.fechanacimiento,'YYYY-MM-DD') fechanacimiento,
              c.telefono, c.email, c.direccion, COALESCE(a.cuentas, 0) cuentas
            FROM clientes c LEFT JOIN nacionalidad n ON n.idnacionalidad = c.idnacionalidad_nacionalidad
            LEFT JOIN cantidades a ON a.cliente = c.idcliente ORDER BY c.nombre, c.idcliente
            """);
    }
    public List<Map<String, Object>> cuentas() {
        return jdbc.queryForList("""
            WITH vinculos AS (
              SELECT idcliente_clientes cliente, idabonado cuenta FROM abonados WHERE idcliente_clientes IS NOT NULL
              UNION SELECT idresponsable, idabonado FROM abonados WHERE idresponsable IS NOT NULL
            )
            SELECT c.idcliente, c.nombre, c.cedula, c.telefono, c.email, a.idabonado cuenta,
              CASE WHEN a.idcliente_clientes = c.idcliente AND a.idresponsable = c.idcliente THEN 'Titular y responsable de pago'
                   WHEN a.idcliente_clientes = c.idcliente THEN 'Titular' ELSE 'Responsable de pago' END relacion,
              a.nromedidor, a.estado, a.direccionubicacion direccion,
              t.nombre titular, r.nombre responsable
            FROM vinculos v JOIN clientes c ON c.idcliente = v.cliente JOIN abonados a ON a.idabonado = v.cuenta
            LEFT JOIN clientes t ON t.idcliente = a.idcliente_clientes
            LEFT JOIN clientes r ON r.idcliente = a.idresponsable
            ORDER BY c.nombre, c.idcliente, a.idabonado
            """);
    }
    public List<Map<String, Object>> pendientes() {
        return jdbc.queryForList("""
            WITH facturas_pendientes AS (
              SELECT f.idcliente, f.idfactura, f.idabonado, f.totaltarifa FROM facturas f
              WHERE f.totaltarifa > 0 AND f.fechaeliminacion IS NULL AND f.fechaanulacion IS NULL
                AND f.fechacobro IS NULL AND COALESCE(f.pagado, 0) = 0 AND f.estado IN (1,2,3)
                AND (f.idmodulo = 27 OR (f.fechaconvenio IS NULL
                  AND COALESCE(f.estadoconvenio,0) <> 1 AND COALESCE(f.conveniopago,0) = 0))
            ), valores AS (
              SELECT f.idcliente, f.idfactura, f.idabonado,
                COALESCE(SUM(ROUND(CAST(r.cantidad * r.valorunitario AS numeric),2)), f.totaltarifa) capital
              FROM facturas_pendientes f LEFT JOIN rubroxfac r ON r.idfactura_facturas = f.idfactura
                AND (r.estado <> 0 OR r.estado IS NULL)
              GROUP BY f.idcliente, f.idfactura, f.idabonado, f.totaltarifa
            ), resumen AS (
              SELECT idcliente, COUNT(*) facturas, COUNT(DISTINCT idabonado) cuentas,
                SUM(capital) capital FROM valores GROUP BY idcliente
            )
            SELECT c.idcliente, c.nombre, c.cedula, c.telefono, c.email, c.direccion,
              COALESCE(r.facturas,0) facturas, COALESCE(r.cuentas,0) cuentas,
              COALESCE(r.capital,0) capital
            FROM clientes c LEFT JOIN resumen r ON r.idcliente = c.idcliente ORDER BY c.nombre, c.idcliente
            """);
    }
}
