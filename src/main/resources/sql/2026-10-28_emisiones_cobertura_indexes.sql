-- Ejecutar fuera de una transaccion sobre PostgreSQL.
-- Permite verificar por emision/cuenta sin recorrer todo el historial de lecturas.
CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_lecturas_emision_abonado_cobertura
    ON public.lecturas (idemision, idabonado_abonados);

CREATE INDEX CONCURRENTLY IF NOT EXISTS idx_abonados_ruta_apertura_1_2
    ON public.abonados (idruta_rutas, idabonado)
    WHERE estado IN (1, 2);
