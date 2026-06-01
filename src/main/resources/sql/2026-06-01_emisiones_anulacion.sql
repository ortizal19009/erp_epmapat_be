ALTER TABLE emisiones
ADD COLUMN IF NOT EXISTS iddocumento_anulacion BIGINT,
ADD COLUMN IF NOT EXISTS documento_anulacion VARCHAR(250),
ADD COLUMN IF NOT EXISTS referencia_documento_anulacion VARCHAR(250),
ADD COLUMN IF NOT EXISTS motivo_anulacion TEXT,
ADD COLUMN IF NOT EXISTS usuario_anulacion BIGINT,
ADD COLUMN IF NOT EXISTS fechaanulacion TIMESTAMPTZ;
