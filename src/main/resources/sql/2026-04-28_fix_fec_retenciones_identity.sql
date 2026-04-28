-- Corrige el esquema de fec_retenciones para que idretencion comparta
-- el mismo identificador de retenciones(idrete) y no sea identity propio.

ALTER TABLE public.fec_retenciones
    ALTER COLUMN idretencion DROP IDENTITY IF EXISTS;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fec_retenciones_retencion_fk'
    ) THEN
        ALTER TABLE public.fec_retenciones
            ADD CONSTRAINT fec_retenciones_retencion_fk
            FOREIGN KEY (idretencion) REFERENCES public.retenciones(idrete) ON DELETE CASCADE;
    END IF;
END $$;
