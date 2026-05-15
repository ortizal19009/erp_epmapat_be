ALTER TABLE public.fec_factura
ADD COLUMN IF NOT EXISTS intentos_autorizacion integer DEFAULT 0,
ADD COLUMN IF NOT EXISTS fecha_ultimo_intento timestamp NULL,
ADD COLUMN IF NOT EXISTS fecha_autorizacion timestamp NULL,
ADD COLUMN IF NOT EXISTS mail_enviado boolean DEFAULT false;

UPDATE public.fec_factura
SET intentos_autorizacion = COALESCE(intentos_autorizacion, 0),
    mail_enviado = COALESCE(mail_enviado, false)
WHERE intentos_autorizacion IS NULL
   OR mail_enviado IS NULL;

CREATE TABLE IF NOT EXISTS public.fec_factura_log (
    id bigserial PRIMARY KEY,
    idfactura bigint NOT NULL,
    estado varchar(5) NOT NULL,
    mensaje text,
    fecha timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP
);
