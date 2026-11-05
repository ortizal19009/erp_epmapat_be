-- Ejecutar antes de desplegar el backend. NULL identifica lecturas historicas sin snapshot.
ALTER TABLE public.lecturas ADD COLUMN IF NOT EXISTS promedio numeric(14,2);
COMMENT ON COLUMN public.lecturas.promedio IS
'Promedio de consumo de los seis meses anteriores, calculado al abrir la emision. Cero si no hay historial valido.';
CREATE INDEX IF NOT EXISTS idx_lecturas_abonado_emision_promedio
ON public.lecturas (idabonado_abonados, idemision, idlectura DESC);
