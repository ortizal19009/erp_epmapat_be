-- Ejecutar antes de desplegar el backend. Los tr?mites hist?ricos conservan NULL.
ALTER TABLE aguatramite ADD COLUMN IF NOT EXISTS idabonado bigint;
CREATE INDEX IF NOT EXISTS idx_aguatramite_idabonado ON aguatramite (idabonado);
-- No inferir cuentas hist?ricas por cliente: un cliente puede tener varias cuentas.
