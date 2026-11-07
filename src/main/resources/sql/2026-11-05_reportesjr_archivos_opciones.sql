-- Aplicar antes de desplegar el backend. No elimina los binarios existentes.
BEGIN;
ALTER TABLE reportesjr ADD COLUMN IF NOT EXISTS jrxml_path varchar(255);
ALTER TABLE reportesjr ADD COLUMN IF NOT EXISTS jasper_path varchar(255);
ALTER TABLE reportesjr ALTER COLUMN jrxml DROP NOT NULL;
ALTER TABLE reportesjr ALTER COLUMN jasper DROP NOT NULL;
CREATE TABLE IF NOT EXISTS reportesjr_opciones (
  idreporte smallint NOT NULL REFERENCES reportesjr(idreporte) ON DELETE CASCADE,
  idrepoxopcion smallint NOT NULL REFERENCES repoxopcion(idrepoxopcion),
  PRIMARY KEY (idreporte, idrepoxopcion)
);
CREATE INDEX IF NOT EXISTS idx_reportesjr_opciones_opcion ON reportesjr_opciones(idrepoxopcion);
-- La opcion principal sigue disponible por compatibilidad; la tabla guarda opciones adicionales.
COMMIT;
