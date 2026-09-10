-- Trazabilidad GPS de lectores. Ejecutar antes de desplegar el backend,
-- porque spring.jpa.hibernate.ddl-auto está configurado en none.

CREATE TABLE IF NOT EXISTS tracking_sessions (
    id VARCHAR(50) PRIMARY KEY,
    reader_id BIGINT NOT NULL,
    route_id BIGINT,
    device_id VARCHAR(100),
    work_date DATE NOT NULL,
    start_time TIMESTAMP,
    end_time TIMESTAMP,
    status VARCHAR(20) NOT NULL DEFAULT 'STARTED',
    total_distance_meters DOUBLE PRECISION NOT NULL DEFAULT 0,
    total_points INTEGER NOT NULL DEFAULT 0,
    total_readings INTEGER NOT NULL DEFAULT 0,
    start_battery_level INTEGER,
    end_battery_level INTEGER,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS tracking_points (
    id BIGSERIAL PRIMARY KEY,
    client_point_id VARCHAR(100),
    tracking_session_id VARCHAR(50) NOT NULL,
    latitude DOUBLE PRECISION NOT NULL,
    longitude DOUBLE PRECISION NOT NULL,
    accuracy DOUBLE PRECISION,
    altitude DOUBLE PRECISION,
    speed DOUBLE PRECISION,
    bearing DOUBLE PRECISION,
    battery_level INTEGER,
    is_mock_location BOOLEAN NOT NULL DEFAULT FALSE,
    route_status VARCHAR(20),
    captured_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_tracking_points_session
        FOREIGN KEY (tracking_session_id) REFERENCES tracking_sessions(id)
);

ALTER TABLE tracking_points ADD COLUMN IF NOT EXISTS client_point_id VARCHAR(100);
CREATE UNIQUE INDEX IF NOT EXISTS uk_tracking_point_client
    ON tracking_points (client_point_id) WHERE client_point_id IS NOT NULL;
CREATE INDEX IF NOT EXISTS idx_tracking_points_session_captured
    ON tracking_points (tracking_session_id, captured_at);
CREATE INDEX IF NOT EXISTS idx_tracking_sessions_reader_work_date
    ON tracking_sessions (reader_id, work_date);

CREATE TABLE IF NOT EXISTS route_deviation_events (
    id BIGSERIAL PRIMARY KEY,
    tracking_session_id VARCHAR(50) NOT NULL,
    reader_id BIGINT,
    exit_time BIGINT,
    return_time BIGINT,
    exit_latitude DOUBLE PRECISION,
    exit_longitude DOUBLE PRECISION,
    maximum_distance_meters DOUBLE PRECISION,
    duration_seconds BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_route_deviation_session
        FOREIGN KEY (tracking_session_id) REFERENCES tracking_sessions(id)
);

CREATE INDEX IF NOT EXISTS idx_route_deviation_session
    ON route_deviation_events (tracking_session_id, exit_time);

CREATE TABLE IF NOT EXISTS gps_gap_events (
    id BIGSERIAL PRIMARY KEY,
    tracking_session_id VARCHAR(50) NOT NULL,
    start_time BIGINT,
    end_time BIGINT,
    duration_seconds BIGINT,
    last_latitude DOUBLE PRECISION,
    last_longitude DOUBLE PRECISION,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_gps_gap_session
        FOREIGN KEY (tracking_session_id) REFERENCES tracking_sessions(id)
);

CREATE INDEX IF NOT EXISTS idx_gps_gap_session
    ON gps_gap_events (tracking_session_id, start_time);
