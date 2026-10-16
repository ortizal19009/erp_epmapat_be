WITH valor_anterior AS (
    SELECT idfactura_facturas, SUM(cantidad * valorunitario) AS total_anterior
    FROM rubroxfac
    WHERE (estado IS NULL OR estado <> 0) AND idrubro_rubros NOT IN (5, 165)
    GROUP BY idfactura_facturas
),
valor_nuevo AS (
    SELECT l2.idfactura, SUM(rf2.cantidad * rf2.valorunitario) AS total_nuevo
    FROM lecturas l2
    JOIN rubroxfac rf2 ON l2.idfactura = rf2.idfactura_facturas
    WHERE (rf2.estado IS NULL OR rf2.estado <> 0) AND rf2.idrubro_rubros NOT IN (5, 165)
    GROUP BY l2.idfactura
)
SELECT
    e.emision,
    c.nombre,
    l.idabonado_abonados AS cuenta,
    f.idfactura AS factura_original,
    f.razoneliminacion,
    f.fechaeliminacion,
    va.total_anterior AS valoranterior,
    l2.idfactura AS factura_nueva,
    vn.total_nuevo AS valornuevo,
    (vn.total_nuevo - va.total_anterior) AS diferencia
FROM lecturas l
JOIN emisiones e ON l.idemision = e.idemision
JOIN facturas f ON l.idfactura = f.idfactura
JOIN abonados a ON l.idabonado_abonados = a.idabonado
JOIN clientes c ON a.idresponsable = c.idcliente
LEFT JOIN emisionindividual ei ON l.idlectura = ei.idlecturaanterior
LEFT JOIN lecturas l2 ON ei.idlecturanueva = l2.idlectura
LEFT JOIN valor_anterior va ON va.idfactura_facturas = l.idfactura
LEFT JOIN valor_nuevo vn ON vn.idfactura = l2.idfactura
WHERE f.fechaeliminacion BETWEEN $P{desde} AND $P{hasta}
ORDER BY c.nombre, e.emision;