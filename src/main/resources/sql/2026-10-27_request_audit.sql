-- Ejecutar antes de desplegar (ddl-auto=none).
CREATE TABLE IF NOT EXISTS request_audit (
 id BIGSERIAL PRIMARY KEY,
 fecha TIMESTAMPTZ NOT NULL,
 metodo VARCHAR(16) NOT NULL,
 ruta VARCHAR(2048) NOT NULL,
 estado INTEGER NOT NULL,
 duracion_ms BIGINT NOT NULL,
 ip VARCHAR(64),
 usuario_id BIGINT
);
CREATE INDEX IF NOT EXISTS idx_request_audit_fecha ON request_audit(fecha DESC);
CREATE INDEX IF NOT EXISTS idx_request_audit_estado ON request_audit(estado, id DESC);
