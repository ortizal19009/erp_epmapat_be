-- Agrega el flujo de aprobacion utilizado por CondMultasIntereses.
-- Los registros historicos se consideran aprobados para conservar su efecto previo.
BEGIN;

ALTER TABLE condmultasintereses
    ADD COLUMN IF NOT EXISTS estado VARCHAR(20);

ALTER TABLE condmultasintereses
    ADD COLUMN IF NOT EXISTS idusaprueba BIGINT;

ALTER TABLE condmultasintereses
    ADD COLUMN IF NOT EXISTS fecaprobacion TIMESTAMP;

ALTER TABLE condmultasintereses
    ADD COLUMN IF NOT EXISTS observacion_aprobacion TEXT;

UPDATE condmultasintereses
SET estado = 'APROBADO'
WHERE estado IS NULL;

ALTER TABLE condmultasintereses
    ALTER COLUMN estado SET DEFAULT 'PENDIENTE';

ALTER TABLE condmultasintereses
    ALTER COLUMN estado SET NOT NULL;

CREATE INDEX IF NOT EXISTS idx_condmultasintereses_factura_estado
    ON condmultasintereses (idfactura_facturas, estado);

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1
        FROM pg_constraint
        WHERE conname = 'fk_condmultasintereses_usuario_aprueba'
    ) THEN
        ALTER TABLE condmultasintereses
            ADD CONSTRAINT fk_condmultasintereses_usuario_aprueba
            FOREIGN KEY (idusaprueba) REFERENCES usuarios (idusuario);
    END IF;
END $$;

COMMIT;
