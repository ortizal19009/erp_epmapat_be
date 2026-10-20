-- Solo lectura. Cambiar las fechas y, opcionalmente, cuenta/factura para revisar un caso.
-- Sin inferir una factura nueva por nombre, importe o cuenta: usar el vinculo explicito.
WITH parametros AS (
    SELECT DATE '2026-09-01' AS desde, DATE '2026-09-30' AS hasta,
           NULL::bigint AS cuenta, NULL::bigint AS factura
)
SELECT l.idabonado_abonados AS cuenta, f.idfactura AS factura_original,
       f.fechaeliminacion, f.razoneliminacion, ei.idlecturanueva,
       ln.idfactura AS factura_nueva, old.total AS facturado, new.total AS refacturado,
       new.total - old.total AS diferencia,
       CASE WHEN ei.idlecturanueva IS NULL THEN 'SIN VINCULO EN EMISIONINDIVIDUAL'
            WHEN ln.idlectura IS NULL THEN 'LECTURA NUEVA INEXISTENTE'
            WHEN ln.idfactura IS NULL THEN 'LECTURA NUEVA SIN FACTURA'
            WHEN new.total IS NULL THEN 'SIN RUBROS ACTIVOS COMPUTABLES EN FACTURA NUEVA'
            ELSE 'CON VALORES' END AS diagnostico
FROM parametros p JOIN facturas f ON f.fechaeliminacion >= p.desde AND f.fechaeliminacion < p.hasta + INTERVAL '1 day'
JOIN lecturas l ON l.idfactura = f.idfactura
LEFT JOIN emisionindividual ei ON ei.idlecturaanterior = l.idlectura
LEFT JOIN lecturas ln ON ln.idlectura = ei.idlecturanueva
LEFT JOIN LATERAL (SELECT SUM(r.cantidad*r.valorunitario) AS total FROM rubroxfac r
    WHERE r.idfactura_facturas=f.idfactura AND (r.estado IS NULL OR r.estado<>0) AND r.idrubro_rubros NOT IN(5,165)) old ON TRUE
LEFT JOIN LATERAL (SELECT SUM(r.cantidad*r.valorunitario) AS total FROM rubroxfac r
    WHERE r.idfactura_facturas=ln.idfactura AND (r.estado IS NULL OR r.estado<>0) AND r.idrubro_rubros NOT IN(5,165)) new ON TRUE
WHERE (p.cuenta IS NULL OR l.idabonado_abonados=p.cuenta) AND (p.factura IS NULL OR f.idfactura=p.factura)
ORDER BY l.idabonado_abonados, f.idfactura;
