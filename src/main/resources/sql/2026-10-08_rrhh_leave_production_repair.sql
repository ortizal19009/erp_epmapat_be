-- Reparacion conjunta de las migraciones TH Leave del 6 de octubre.
-- Ejecutar SOLO en ErpEpmapaT, con respaldo y aprobacion para produccion.
-- psql -X -v ON_ERROR_STOP=1 -f 2026-10-08_rrhh_leave_production_repair.sql
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';
SET LOCAL search_path = public;
DO $$ BEGIN
 IF current_database() <> 'ErpEpmapaT' THEN
  RAISE EXCEPTION 'Base incorrecta: %, se requiere ErpEpmapaT', current_database();
 END IF;
 IF to_regclass('public.th_leave_balances') IS NULL
 OR to_regclass('public.th_leave_requests') IS NULL
 OR to_regclass('public.th_audit_log') IS NULL THEN
  RAISE EXCEPTION 'Faltan tablas base de RRHH; no continuar';
 END IF;
END $$;
-- Fuente: 2026-10-06_rrhh_leave_movements.sql
-- Apply before deploying the second TH Leave delivery. Existing approvals are not backfilled.

ALTER TABLE th_leave_requests ADD COLUMN IF NOT EXISTS resuelto_por BIGINT;
ALTER TABLE th_leave_requests ADD COLUMN IF NOT EXISTS fecha_resolucion TIMESTAMP;
ALTER TABLE th_leave_requests ADD COLUMN IF NOT EXISTS motivo_resolucion TEXT;

CREATE TABLE IF NOT EXISTS th_leave_movements (
    idmovement BIGSERIAL PRIMARY KEY,
    idbalance BIGINT NOT NULL REFERENCES th_leave_balances(idbalance),
    idrequest BIGINT REFERENCES th_leave_requests(idrequest),
    idmovement_origen BIGINT UNIQUE REFERENCES th_leave_movements(idmovement),
    tipo VARCHAR(20) NOT NULL,
    dias NUMERIC(10,2) NOT NULL,
    disponibles_antes NUMERIC(10,2) NOT NULL,
    disponibles_despues NUMERIC(10,2) NOT NULL,
    usados_antes NUMERIC(10,2) NOT NULL,
    usados_despues NUMERIC(10,2) NOT NULL,
    asignados NUMERIC(10,2) NOT NULL,
    usuario BIGINT NOT NULL,
    fecha TIMESTAMP NOT NULL,
    motivo TEXT,
    CONSTRAINT uq_th_leave_movements_request_type UNIQUE (idrequest, tipo),
    CONSTRAINT chk_th_leave_movement_kind CHECK (
        (tipo = 'APERTURA' AND dias >= 0 AND idrequest IS NULL AND idmovement_origen IS NULL)
        OR (tipo = 'CONSUMO' AND dias < 0 AND idrequest IS NOT NULL AND idmovement_origen IS NULL)
        OR (tipo = 'REINTEGRO' AND dias > 0 AND idrequest IS NOT NULL AND idmovement_origen IS NOT NULL)
    ),
    CONSTRAINT chk_th_leave_movement_balance CHECK (
        disponibles_antes >= 0 AND disponibles_despues >= 0 AND usados_antes >= 0 AND usados_despues >= 0
        AND asignados >= 0 AND disponibles_despues = disponibles_antes + dias
        AND asignados = disponibles_despues + usados_despues
        AND ((tipo = 'APERTURA' AND disponibles_antes = 0 AND usados_antes = usados_despues)
            OR (tipo IN ('CONSUMO', 'REINTEGRO') AND usados_despues = usados_antes - dias))
    )
);

CREATE UNIQUE INDEX IF NOT EXISTS uq_th_leave_movements_opening
    ON th_leave_movements (idbalance) WHERE tipo = 'APERTURA';
CREATE INDEX IF NOT EXISTS idx_th_leave_movements_balance
    ON th_leave_movements (idbalance, idmovement DESC);


-- Fuente: 2026-10-06_rrhh_leave_balance_adjustments.sql
-- Apply after 2026-10-06_rrhh_leave_movements.sql, before deploying adjustments.
ALTER TABLE th_leave_movements ADD COLUMN IF NOT EXISTS clave VARCHAR(36);
CREATE UNIQUE INDEX IF NOT EXISTS uq_th_leave_movement_key ON th_leave_movements (idbalance, clave) WHERE clave IS NOT NULL;
ALTER TABLE th_leave_movements DROP CONSTRAINT IF EXISTS chk_th_leave_movement_kind,
DROP CONSTRAINT IF EXISTS chk_th_leave_movement_balance;
ALTER TABLE th_leave_movements
ADD CONSTRAINT chk_th_leave_movement_kind CHECK (
        (tipo = 'APERTURA' AND dias >= 0 AND idrequest IS NULL AND idmovement_origen IS NULL)
        OR (tipo = 'CONSUMO' AND dias < 0 AND idrequest IS NOT NULL AND idmovement_origen IS NULL)
        OR (tipo = 'AJUSTE' AND dias <> 0 AND idrequest IS NULL AND idmovement_origen IS NULL AND clave IS NOT NULL AND motivo IS NOT NULL AND length(trim(motivo)) > 0)
        OR (tipo = 'REINTEGRO' AND dias > 0 AND idrequest IS NOT NULL AND idmovement_origen IS NOT NULL)
    ),
ADD CONSTRAINT chk_th_leave_movement_balance CHECK (
        disponibles_antes >= 0 AND disponibles_despues >= 0 AND usados_antes >= 0 AND usados_despues >= 0
        AND asignados >= 0 AND disponibles_despues = disponibles_antes + dias
        AND asignados = disponibles_despues + usados_despues
        AND ((tipo = 'APERTURA' AND disponibles_antes = 0 AND usados_antes = usados_despues)
            OR (tipo = 'AJUSTE' AND usados_antes = usados_despues)
            OR (tipo IN ('CONSUMO', 'REINTEGRO') AND usados_despues = usados_antes - dias))
    );

-- Fuente: 2026-10-06_rrhh_leave_balance_version.sql
-- Apply after the movements and adjustments migrations, before deploying balance state controls.
ALTER TABLE th_audit_log ALTER COLUMN detalle TYPE TEXT;
ALTER TABLE th_leave_balances ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE th_leave_balances ALTER COLUMN version SET DEFAULT 0;
UPDATE th_leave_balances SET version = 0 WHERE version IS NULL;
ALTER TABLE th_leave_balances ALTER COLUMN version SET NOT NULL;
ALTER TABLE th_leave_balances DROP CONSTRAINT IF EXISTS chk_th_leave_balance_version;
ALTER TABLE th_leave_balances ADD CONSTRAINT chk_th_leave_balance_version CHECK (version >= 0);
-- Verificacion dentro de la misma transaccion: ante error no hay cambios parciales.
DO $$ BEGIN
 IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='th_leave_balances' AND column_name='version' AND data_type='bigint' AND is_nullable='NO')
 OR NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_schema='public' AND table_name='th_leave_movements' AND column_name='clave')
 OR (SELECT count(*) FROM information_schema.columns WHERE table_schema='public' AND table_name='th_leave_requests' AND column_name IN ('resuelto_por','fecha_resolucion','motivo_resolucion')) <> 3 THEN
  RAISE EXCEPTION 'Verificacion de migraciones RRHH fallida';
 END IF;
END $$;
COMMIT;
SELECT current_database() AS base, to_regclass('public.th_leave_movements') AS movimientos;
SELECT table_name,column_name,data_type,is_nullable FROM information_schema.columns
 WHERE table_schema='public' AND (table_name='th_leave_balances' AND column_name='version'
 OR table_name='th_leave_requests' AND column_name IN ('resuelto_por','fecha_resolucion','motivo_resolucion')
 OR table_name='th_leave_movements' AND column_name='clave') ORDER BY table_name,column_name;
