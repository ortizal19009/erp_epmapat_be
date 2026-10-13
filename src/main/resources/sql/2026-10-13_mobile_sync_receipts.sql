-- Expansivo. Revisar y respaldar antes de aplicar; no borrar recibos al revertir codigo.
CREATE TABLE IF NOT EXISTS mobile_reading_receipts (
    operation_kind varchar(16) NOT NULL DEFAULT 'READING',
    result_path text,
    operation_id varchar(36) PRIMARY KEY,
    reader_id bigint NOT NULL,
    reading_id bigint NOT NULL,
    payload_hash varchar(160) NOT NULL,
    confirmed_at timestamptz NOT NULL
);
CREATE INDEX IF NOT EXISTS mobile_reading_receipts_reading_idx ON mobile_reading_receipts(reading_id);

ALTER TABLE route_deviation_events ADD COLUMN IF NOT EXISTS client_event_id varchar(120);
ALTER TABLE gps_gap_events ADD COLUMN IF NOT EXISTS client_event_id varchar(120);
CREATE UNIQUE INDEX IF NOT EXISTS uk_route_deviation_client_event ON route_deviation_events(client_event_id);
CREATE UNIQUE INDEX IF NOT EXISTS uk_gps_gap_client_event ON gps_gap_events(client_event_id);

ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS reading_source varchar(32);
ALTER TABLE lecturas ADD COLUMN IF NOT EXISTS reading_capture_age_millis bigint;
