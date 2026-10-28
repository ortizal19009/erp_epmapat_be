-- Ejecutar manualmente en PostgreSQL, fuera de una transacci?n.
-- Acelera el filtro por ruta y el orden de las p?ginas de abonados (estados 1 y 2).
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_abonados_ruta_cuenta_activos
    ON public.abonados (idruta_rutas, idabonado)
    WHERE estado IN (1, 2);
