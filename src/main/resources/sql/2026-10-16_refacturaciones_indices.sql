-- Ejecutar fuera de una transaccion, en la BD del reporte.
-- Revisar antes pg_indexes para no duplicar indices equivalentes con otro nombre.
-- CONCURRENTLY permite mantener disponibles las escrituras durante la construccion.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refacturaciones_fechaeliminacion
    ON facturas (fechaeliminacion) WHERE fechaeliminacion IS NOT NULL;
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refacturaciones_lecturas_factura
    ON lecturas (idfactura);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refacturaciones_lectura_anterior
    ON emisionindividual (idlecturaanterior);
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_refacturaciones_rubros_factura_activos
    ON rubroxfac (idfactura_facturas)
    WHERE (estado IS NULL OR estado <> 0) AND idrubro_rubros NOT IN (5, 165);
