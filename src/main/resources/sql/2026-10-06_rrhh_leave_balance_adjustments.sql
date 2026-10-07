-- Apply after 2026-10-06_rrhh_leave_movements.sql, before deploying adjustments.
BEGIN;
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
COMMIT;
