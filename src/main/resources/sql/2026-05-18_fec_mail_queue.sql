ALTER TABLE public.fec_factura
ADD COLUMN IF NOT EXISTS mail_intentos integer DEFAULT 0,
ADD COLUMN IF NOT EXISTS mail_error text NULL,
ADD COLUMN IF NOT EXISTS email_estado varchar(30) DEFAULT 'NO_ENVIADO',
ADD COLUMN IF NOT EXISTS fecha_reenvio timestamp NULL;

UPDATE public.fec_factura
SET mail_intentos = COALESCE(mail_intentos, 0),
    email_estado = COALESCE(email_estado,
      CASE
        WHEN COALESCE(mail_enviado, false) = true THEN 'ENVIADO'
        ELSE 'NO_ENVIADO'
      END)
WHERE mail_intentos IS NULL
   OR email_estado IS NULL;

CREATE TABLE IF NOT EXISTS public.fec_mail_queue(
   id bigserial PRIMARY KEY,
   idfactura bigint NOT NULL,
   correo varchar(300) NOT NULL,
   estado varchar(30) NOT NULL DEFAULT 'PENDIENTE',
   intentos integer NOT NULL DEFAULT 0,
   ultimo_error text NULL,
   fecha_crea timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
   fecha_envio timestamp NULL,
   usuario_solicita bigint NULL,
   prioridad integer NOT NULL DEFAULT 1,
   correlation_id varchar(200) NULL,
   ip_solicita varchar(80) NULL
);

CREATE INDEX IF NOT EXISTS fec_mail_queue_estado_fecha_idx
   ON public.fec_mail_queue (estado, prioridad desc, fecha_crea asc);

CREATE INDEX IF NOT EXISTS fec_mail_queue_factura_idx
   ON public.fec_mail_queue (idfactura, fecha_crea desc);
