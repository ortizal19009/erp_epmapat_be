package com.epmapat.erp_epmapat.configuracion;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/** Keeps existing GPS tables compatible with point/event deduplication and historical sessions. */
@Component
public class TrackingSchemaMigration implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(TrackingSchemaMigration.class);

    private final JdbcTemplate jdbcTemplate;

    public TrackingSchemaMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        jdbcTemplate.execute("ALTER TABLE tracking_sessions "
                + "ADD COLUMN IF NOT EXISTS source_session_id VARCHAR(50)");
        jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS tracking_sessions_source_idx "
                + "ON tracking_sessions (source_session_id)");
        jdbcTemplate.execute("ALTER TABLE IF EXISTS tracking_points "
                + "ADD COLUMN IF NOT EXISTS client_point_id VARCHAR(100)");
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_tracking_point_client "
                + "ON tracking_points (client_point_id) WHERE client_point_id IS NOT NULL");
        jdbcTemplate.execute("ALTER TABLE route_deviation_events "
                + "ADD COLUMN IF NOT EXISTS client_event_id VARCHAR(120)");
        jdbcTemplate.execute("ALTER TABLE gps_gap_events "
                + "ADD COLUMN IF NOT EXISTS client_event_id VARCHAR(120)");
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_route_deviation_client_event "
                + "ON route_deviation_events (client_event_id)");
        jdbcTemplate.execute("CREATE UNIQUE INDEX IF NOT EXISTS uk_gps_gap_client_event "
                + "ON gps_gap_events (client_event_id)");
        log.info("Esquema de trazabilidad GPS verificado");
    }
}
