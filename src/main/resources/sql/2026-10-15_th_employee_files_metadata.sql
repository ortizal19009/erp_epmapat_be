-- Aplicar antes de ejecutar la nueva versión de TH Files. Conserva documentos existentes.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';
SET LOCAL search_path = public;
ALTER TABLE th_employee_files
 ADD COLUMN IF NOT EXISTS descripcion VARCHAR(2000),
 ADD COLUMN IF NOT EXISTS numero_documento VARCHAR(120),
 ADD COLUMN IF NOT EXISTS emisor VARCHAR(150),
 ADD COLUMN IF NOT EXISTS fecha_emision DATE,
 ADD COLUMN IF NOT EXISTS fecha_vencimiento DATE,
 ADD COLUMN IF NOT EXISTS origen VARCHAR(20),
 ADD COLUMN IF NOT EXISTS nombre_original VARCHAR(255),
 ADD COLUMN IF NOT EXISTS content_type VARCHAR(120),
 ADD COLUMN IF NOT EXISTS tamano_bytes BIGINT;
COMMIT;
