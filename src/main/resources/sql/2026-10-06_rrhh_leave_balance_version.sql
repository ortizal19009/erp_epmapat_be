-- Apply after the movements and adjustments migrations, before deploying balance state controls.
BEGIN;
ALTER TABLE th_audit_log ALTER COLUMN detalle TYPE TEXT;
ALTER TABLE th_leave_balances ADD COLUMN IF NOT EXISTS version BIGINT NOT NULL DEFAULT 0;
ALTER TABLE th_leave_balances ALTER COLUMN version SET DEFAULT 0;
UPDATE th_leave_balances SET version = 0 WHERE version IS NULL;
ALTER TABLE th_leave_balances ALTER COLUMN version SET NOT NULL;
ALTER TABLE th_leave_balances DROP CONSTRAINT IF EXISTS chk_th_leave_balance_version;
ALTER TABLE th_leave_balances ADD CONSTRAINT chk_th_leave_balance_version CHECK (version >= 0);
COMMIT;
