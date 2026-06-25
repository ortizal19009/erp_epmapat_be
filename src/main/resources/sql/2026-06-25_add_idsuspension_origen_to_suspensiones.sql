ALTER TABLE suspensiones
ADD COLUMN IF NOT EXISTS idsuspension_origen bigint NULL;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_suspensiones_origen'
    ) THEN
        ALTER TABLE suspensiones
        ADD CONSTRAINT fk_suspensiones_origen
        FOREIGN KEY (idsuspension_origen) REFERENCES suspensiones(idsuspension);
    END IF;
END $$;
