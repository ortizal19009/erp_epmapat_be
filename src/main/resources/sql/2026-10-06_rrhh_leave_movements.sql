-- Apply before deploying the second TH Leave delivery. Existing approvals are not backfilled.
BEGIN;

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

COMMIT;
