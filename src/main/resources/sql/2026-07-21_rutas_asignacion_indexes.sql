-- Mejora consultas de asignacion de rutas y busqueda por estado/descripcion.
-- Ejecutar en PostgreSQL sobre la base activa del backend.

CREATE INDEX IF NOT EXISTS idx_rutas_estado_descripcion
    ON public.rutas (estado, descripcion);

CREATE INDEX IF NOT EXISTS idx_rutas_codigo
    ON public.rutas (codigo);

CREATE INDEX IF NOT EXISTS idx_rutas_descripcion_lower
    ON public.rutas (LOWER(descripcion));

CREATE INDEX IF NOT EXISTS idx_rutas_codigo_lower
    ON public.rutas (LOWER(codigo));

CREATE INDEX IF NOT EXISTS idx_usrxrutas_emision
    ON public.usrxrutas (idemision_emisiones);

CREATE INDEX IF NOT EXISTS idx_usrxrutas_rutas_gin
    ON public.usrxrutas
    USING GIN (rutas jsonb_path_ops);
