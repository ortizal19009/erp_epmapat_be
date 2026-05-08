-- Controles empresariales para retenciones electronicas SRI.
-- Ejecutar despues de 2026-04-21_retenciones_sri_schema.sql.

CREATE UNIQUE INDEX IF NOT EXISTS fec_retenciones_claveacceso_uidx
    ON public.fec_retenciones (claveacceso)
    WHERE claveacceso IS NOT NULL AND btrim(claveacceso) <> '';

CREATE TABLE IF NOT EXISTS public.fec_retenciones_logs (
    idlog bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    idretencion bigint NOT NULL,
    evento varchar(80) NOT NULL,
    estado_anterior varchar(40) NULL,
    estado_nuevo varchar(40) NULL,
    detalle text NULL,
    usuario varchar(120) NULL,
    ip varchar(64) NULL,
    fecha timestamp NOT NULL DEFAULT now(),
    CONSTRAINT fec_retenciones_logs_pk PRIMARY KEY (idlog),
    CONSTRAINT fec_retenciones_logs_retencion_fk
        FOREIGN KEY (idretencion) REFERENCES public.retenciones(idrete) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.fec_retenciones_historial (
    idhistorial bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    idretencion bigint NOT NULL,
    accion varchar(80) NOT NULL,
    datos_anteriores text NULL,
    datos_nuevos text NULL,
    usuario varchar(120) NULL,
    ip varchar(64) NULL,
    fecha timestamp NOT NULL DEFAULT now(),
    CONSTRAINT fec_retenciones_historial_pk PRIMARY KEY (idhistorial),
    CONSTRAINT fec_retenciones_historial_retencion_fk
        FOREIGN KEY (idretencion) REFERENCES public.retenciones(idrete) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS public.sri_respuestas (
    idrespuesta bigint GENERATED ALWAYS AS IDENTITY NOT NULL,
    idretencion bigint NOT NULL,
    claveacceso varchar(49) NULL,
    etapa varchar(40) NOT NULL,
    estado varchar(40) NULL,
    codigo_http integer NULL,
    respuesta text NULL,
    mensaje text NULL,
    fecha timestamp NOT NULL DEFAULT now(),
    CONSTRAINT sri_respuestas_pk PRIMARY KEY (idrespuesta),
    CONSTRAINT sri_respuestas_retencion_fk
        FOREIGN KEY (idretencion) REFERENCES public.retenciones(idrete) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS fec_retenciones_estado_idx
    ON public.fec_retenciones (estado);

CREATE INDEX IF NOT EXISTS fec_retenciones_fechaemision_idx
    ON public.fec_retenciones (fechaemision);

CREATE INDEX IF NOT EXISTS sri_respuestas_claveacceso_idx
    ON public.sri_respuestas (claveacceso);
