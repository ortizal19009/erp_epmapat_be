-- Apply before deploying backend 1.1.6 support. Keeps existing sessions and points.
ALTER TABLE tracking_sessions ADD COLUMN IF NOT EXISTS source_session_id varchar(50);
CREATE INDEX IF NOT EXISTS tracking_sessions_source_idx ON tracking_sessions(source_session_id);
